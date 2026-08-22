package com.example.basecamp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class NeedsProfileSetup(val userId: String, val email: String, val name: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val client: SupabaseClient = supabaseClient
    init {
        viewModelScope.launch {
            supabaseClient.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        handleGoogleLoginSuccess()
                    }
                    else -> {}
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("User not found")
                val user = supabaseClient.postgrest["users"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<User>()
                
                _authState.value = AuthState.Success(user.role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.userFriendlyMessage("Login failed"))
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                supabaseClient.auth.signInWith(Google)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.userFriendlyMessage("Google Sign-in failed"))
            }
        }
    }


    fun handleGoogleLoginSuccess() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("User not found")
                
                // Check if user exists in our public table
                val existingUserList = supabaseClient.postgrest["users"]
                    .select { filter { eq("id", userId) } }
                    .decodeList<User>()
                
                if (existingUserList.isNotEmpty()) {
                    _authState.value = AuthState.Success(existingUserList.first().role)
                } else {
                    // New Google user, need profile setup
                    val email = supabaseClient.auth.currentUserOrNull()?.email ?: ""
                    val name = supabaseClient.auth.currentUserOrNull()?.userMetadata?.get("full_name")?.toString() ?: ""
                    _authState.value = AuthState.NeedsProfileSetup(userId, email, name)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.userFriendlyMessage("Login failed"))
            }
        }
    }

    fun completeGoogleSignup(userId: String, email: String, name: String, role: String, phone: String, website: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val newUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    role = role,
                                        phone = if (role == "Organization") phone else null,
                    website = if (role == "Organization") website else null
                )
                supabaseClient.postgrest["users"].insert(newUser)
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.userFriendlyMessage("Failed to complete signup"))
            }
        }
    }

    fun signup(name: String, email: String, password: String, role: String, phone: String? = null, website: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                supabaseClient.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("User not found")
                
                val newUser = User(
                    id = userId,
                    name = name,
                    role = role,
                    email = email,
                    phone = phone,
                    website = website
                )
                
                supabaseClient.postgrest["users"].insert(newUser)
                
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.userFriendlyMessage("Signup failed"))
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    private fun Exception.userFriendlyMessage(default: String): String {
        val msg = this.message ?: return default
        return msg.substringBefore("URL:").substringBefore("HTTP request to").trim()
    }
}

