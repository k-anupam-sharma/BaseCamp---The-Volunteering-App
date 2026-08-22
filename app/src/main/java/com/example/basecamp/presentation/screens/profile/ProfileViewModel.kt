package com.example.basecamp.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.domain.model.User
import com.example.basecamp.domain.model.Ticket
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()
    
    private val _updateState = MutableStateFlow<Boolean>(false)
    val updateState: StateFlow<Boolean> = _updateState.asStateFlow()

    private val _rsvpCount = MutableStateFlow<Int>(0)
    val rsvpCount: StateFlow<Int> = _rsvpCount.asStateFlow()

    private val _logoutState = MutableStateFlow<Boolean>(false)
    val logoutState: StateFlow<Boolean> = _logoutState.asStateFlow()

    val currentUserId: String
        get() = supabaseClient.auth.currentUserOrNull()?.id ?: "unknown"

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("Not logged in")
                val user = supabaseClient.postgrest["users"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<User>()
                    
                val tickets = supabaseClient.postgrest["tickets"]
                    .select { filter { eq("volunteer_id", userId) } }
                    .decodeList<Ticket>()
                    
                _rsvpCount.value = tickets.size
                _profileState.value = ProfileState.Success(user)
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to fetch profile")
            }
        }
    }

    fun updateProfile(name: String, phone: String, website: String) {
        viewModelScope.launch {
            try {
                val userId = supabaseClient.auth.currentUserOrNull()?.id ?: throw Exception("Not logged in")
                
                // Get current user to preserve role and email
                val currentUserState = _profileState.value
                if (currentUserState is ProfileState.Success) {
                    val updatedUser = currentUserState.user.copy(
                        name = name,
                        phone = phone,
                        website = website
                    )
                    
                    supabaseClient.postgrest["users"].update(
                        {
                            set("name", updatedUser.name)
                            set("phone", updatedUser.phone)
                            set("website", updatedUser.website)
                        }
                    ) {
                        filter { eq("id", userId) }
                    }
                    
                    _profileState.value = ProfileState.Success(updatedUser)
                    _updateState.value = true
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }
    
    fun resetUpdateState() {
        _updateState.value = false
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                supabaseClient.auth.signOut()
            } catch (e: Exception) {
                // Ignore error on logout
            } finally {
                _logoutState.value = true
            }
        }
    }
}
