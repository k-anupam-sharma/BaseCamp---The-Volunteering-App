import sys
content = open('app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt', 'r', encoding='utf-8').read()

imports = """import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.providers.Google
"""
content = content.replace("import io.github.jan.supabase.SupabaseClient", imports + "import io.github.jan.supabase.SupabaseClient")

init_block = """
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

    fun loginWithGoogle() {
        viewModelScope.launch {
            try {
                supabaseClient.auth.signInWith(Google)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google Sign-in failed")
            }
        }
    }
"""

content = content.replace("val client: SupabaseClient = supabaseClient", "val client: SupabaseClient = supabaseClient" + init_block)

open('app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt', 'w', encoding='utf-8').write(content)
