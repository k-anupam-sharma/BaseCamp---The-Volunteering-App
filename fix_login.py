import sys
content = open('app/src/main/java/com/example/basecamp/presentation/auth/LoginScreen.kt', 'r', encoding='utf-8').read()

imports = """import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistTextField
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult"""

content = content.replace("""import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistTextField""", imports)

ui_code = """        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = Color.Black)
        } else {
            val action = viewModel.client.composeAuth.rememberSignInWithGoogle(
                onResult = { result ->
                    when (result) {
                        is NativeSignInResult.Success -> viewModel.handleGoogleLoginSuccess()
                        else -> {}
                    }
                },
                fallback = {}
            )

            BrutalistButton(
                text = "LOG IN",
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BrutalistButton(
                text = "CONTINUE WITH GOOGLE",
                onClick = { action.startFlow() },
                backgroundColor = Color.White,
                textColor = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
        }"""

content = content.replace("""        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = Color.Black)
        } else {
            BrutalistButton(
                text = "LOG IN",
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            )
        }""", ui_code)

open('app/src/main/java/com/example/basecamp/presentation/auth/LoginScreen.kt', 'w', encoding='utf-8').write(content)
