package com.basecamp.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.basecamp.app.presentation.components.BrutalistButton
import com.basecamp.app.presentation.components.BrutalistTextField

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0)) // Off-White/Beige from Design.md
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BASECAMP",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "LOG IN TO CONTINUE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(48.dp))

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
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = Color.Black)
        } else {
            BrutalistButton(
                text = "LOG IN",
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = Color(0xFFFF007F), // Hot Pink for errors
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BrutalistButton(
            text = "CREATE ACCOUNT",
            onClick = onNavigateToSignup,
            backgroundColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
