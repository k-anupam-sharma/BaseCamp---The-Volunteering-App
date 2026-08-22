import sys

def fix_screen(path):
    content = open(path, 'r', encoding='utf-8').read()
    
    # Replace action definition entirely
    import re
    content = re.sub(r'val action = viewModel\.client\.composeAuth\.rememberLoginWith\([\s\S]*?\n\s*\)', '', content)
    
    # Replace button onClick
    content = content.replace("onClick = { action.startFlow() }", "onClick = { viewModel.loginWithGoogle() }")
    
    # Remove unused imports
    content = content.replace("import io.github.jan.supabase.compose.auth.composable.rememberLoginWith\n", "")
    content = content.replace("import io.github.jan.supabase.gotrue.providers.Google\n", "")
    content = content.replace("import io.github.jan.supabase.compose.auth.composeAuth\n", "")
    content = content.replace("import io.github.jan.supabase.compose.auth.composable.NativeSignInResult\n", "")
    
    open(path, 'w', encoding='utf-8').write(content)

fix_screen('app/src/main/java/com/example/basecamp/presentation/auth/LoginScreen.kt')
fix_screen('app/src/main/java/com/example/basecamp/presentation/auth/SignupScreen.kt')
