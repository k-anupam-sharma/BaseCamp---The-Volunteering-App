import sys

path = 'app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt'
content = open(path, 'r', encoding='utf-8').read()

# Add NeedsProfileSetup to AuthState
if "NeedsProfileSetup" not in content:
    content = content.replace(
        "data class Success(val role: String) : AuthState()",
        "data class Success(val role: String) : AuthState()\n    data class NeedsProfileSetup(val userId: String, val email: String, val name: String) : AuthState()"
    )

# Modify handleGoogleLoginSuccess
new_handle = """    fun handleGoogleLoginSuccess() {
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
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun completeGoogleSignup(userId: String, email: String, name: String, role: String, organizationName: String, phone: String, website: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val newUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    role = role,
                    organizationName = if (role == "Organization") organizationName else null,
                    phone = if (role == "Organization") phone else null,
                    website = if (role == "Organization") website else null
                )
                supabaseClient.postgrest["users"].insert(newUser)
                _authState.value = AuthState.Success(role)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to complete signup")
            }
        }
    }"""

import re
content = re.sub(r'    fun handleGoogleLoginSuccess\(\) \{.*?(?=    fun signup)', new_handle + "\n\n", content, flags=re.DOTALL)

open(path, 'w', encoding='utf-8').write(content)
