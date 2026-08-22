import sys

def fix_file(path):
    content = open(path, 'r', encoding='utf-8').read()
    
    # Replace imports
    content = content.replace("import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle", "import io.github.jan.supabase.compose.auth.composable.rememberLoginWith\nimport io.github.jan.supabase.gotrue.providers.Google")
    
    # Replace action definition
    old_action = """val action = viewModel.client.composeAuth.rememberSignInWithGoogle(
                onResult = { result ->
                    when (result) {
                        is NativeSignInResult.Success -> viewModel.handleGoogleLoginSuccess()
                        else -> {}
                    }
                },
                fallback = {}
            )"""
            
    new_action = """val action = viewModel.client.composeAuth.rememberLoginWith(
                provider = Google,
                onResult = { result ->
                    when (result) {
                        is NativeSignInResult.Success -> viewModel.handleGoogleLoginSuccess()
                        else -> {}
                    }
                }
            )"""
            
    content = content.replace(old_action, new_action)
    open(path, 'w', encoding='utf-8').write(content)

fix_file('app/src/main/java/com/example/basecamp/presentation/auth/LoginScreen.kt')
fix_file('app/src/main/java/com/example/basecamp/presentation/auth/SignupScreen.kt')
