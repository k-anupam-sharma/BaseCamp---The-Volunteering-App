package com.example.basecamp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
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
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val client: SupabaseClient = supabaseClient

    fun handleGoogleLoginSuccess() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("User not found")
                
                // Check if user exists in our public table
                val existingUserList = supabaseClient.postgrest["users"]
                    .select { filter { eq("id", userId) } }
                    .decodeList<User>()
                
                val role = if (existingUserList.isNotEmpty()) {
                    existingUserList.first().role
                } else {
                    // New Google user, default to Volunteer
                    val newUser = User(
                        id = userId,
                        name = supabaseClient.auth.currentUserOrNull()?.userMetadata?.get("full_name")?.toString() ?: "Volunteer",
                        role = "Volunteer",
                        email = supabaseClient.auth.currentUserOrNull()?.email ?: ""
                    )
                    supabaseClient.postgrest["users"].insert(newUser)
                    "Volunteer"
                }
                
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google Login failed")
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
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String, password: String, role: String) {
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
                    email = email
                )
                
                supabaseClient.postgrest["users"].insert(newUser)
                
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

