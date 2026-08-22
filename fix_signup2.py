import sys
content = open('app/src/main/java/com/example/basecamp/presentation/auth/SignupScreen.kt', 'r', encoding='utf-8').read()

imports = """import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistTextField
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult"""

content = content.replace("var name by remember { mutableStateOf(\"\") }", "var name by remember { mutableStateOf(\"\") }\n    var phone by remember { mutableStateOf(\"\") }\n    var website by remember { mutableStateOf(\"\") }")

ui_fields_old = """        BrutalistTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "EMAIL ADDRESS"
        )

        Spacer(modifier = Modifier.height(16.dp))

        BrutalistTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "PASSWORD",
            isPassword = true
        )"""

ui_fields_new = """        if (selectedRole == "Volunteer") {
            BrutalistTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "FULL NAME"
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            BrutalistTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "ORGANIZATION NAME"
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrutalistTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "PHONE NUMBER (OPTIONAL)"
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrutalistTextField(
                value = website,
                onValueChange = { website = it },
                placeholder = "WEBSITE (OPTIONAL)"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        BrutalistTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "EMAIL ADDRESS"
        )

        Spacer(modifier = Modifier.height(16.dp))

        BrutalistTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "PASSWORD",
            isPassword = true
        )"""

content = content.replace(ui_fields_old, ui_fields_new)
content = content.replace("viewModel.signup(name, email, password, selectedRole)", "viewModel.signup(name, email, password, selectedRole, phone.takeIf { selectedRole == \"Organization\" }, website.takeIf { selectedRole == \"Organization\" })")

open('app/src/main/java/com/example/basecamp/presentation/auth/SignupScreen.kt', 'w', encoding='utf-8').write(content)
