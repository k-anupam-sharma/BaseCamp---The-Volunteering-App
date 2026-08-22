import sys
import re

def add_routing(path):
    content = open(path, 'r', encoding='utf-8').read()
    
    if "onNeedsProfileSetup: (String, String, String) -> Unit," not in content:
        # add to signature
        content = re.sub(r'(fun (Login|Signup)Screen\(\n.*?)(viewModel: AuthViewModel = hiltViewModel\(\))', r'\1onNeedsProfileSetup: (String, String, String) -> Unit,\n    \3', content, flags=re.DOTALL)
        
        # update LaunchedEffect
        effect = """    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val role = (authState as AuthState.Success).role
            onLoginSuccess(role) // or onSignupSuccess
        } else if (authState is AuthState.NeedsProfileSetup) {
            val state = authState as AuthState.NeedsProfileSetup
            onNeedsProfileSetup(state.userId, state.email, state.name)
        }
    }"""
        if "onSignupSuccess" in content:
            effect = effect.replace("onLoginSuccess", "onSignupSuccess")
            
        content = re.sub(r'    LaunchedEffect\(authState\) \{.*?\n    \}', effect, content, flags=re.DOTALL)
        open(path, 'w', encoding='utf-8').write(content)

add_routing('app/src/main/java/com/example/basecamp/presentation/auth/LoginScreen.kt')
add_routing('app/src/main/java/com/example/basecamp/presentation/auth/SignupScreen.kt')
