import sys

path = 'app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt'
content = open(path, 'r', encoding='utf-8').read()

login_func = """
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
"""

if "fun login(" not in content:
    content = content.replace("fun loginWithGoogle()", login_func.strip() + "\n\n    fun loginWithGoogle()")

open(path, 'w', encoding='utf-8').write(content)
