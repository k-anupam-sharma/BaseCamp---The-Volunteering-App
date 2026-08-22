package com.example.baseccamp.presentation.auth

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
import com.example.baseccamp.presentation.components.BrutalistButton
import com.example.baseccamp.presentation.components.BrutalistTextField

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Volunteer") } // "Volunteer" or "Organization"

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignupSuccess((authState as AuthState.Success).role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "JOIN BASECAMP",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Role Selection
        Text(
            text = "SELECT YOUR ROLE:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val volColor = if (selectedRole == "Volunteer") Color(0xFFFAFF00) else Color.White
            val orgColor = if (selectedRole == "Organization") Color(0xFFFAFF00) else Color.White

            BrutalistButton(
                text = "VOLUNTEER",
                onClick = { selectedRole = "Volunteer" },
                backgroundColor = volColor,
                modifier = Modifier.weight(1f)
            )
            BrutalistButton(
                text = "ORGANIZATION",
                onClick = { selectedRole = "Organization" },
                backgroundColor = orgColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                text = "SIGN UP",
                onClick = { viewModel.signup(email, password, selectedRole) },
                backgroundColor = Color(0xFF00E5FF), // Bright Cyan for primary action
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = Color(0xFFFF007F), // Hot Pink
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BrutalistButton(
            text = "BACK TO LOGIN",
            onClick = onNavigateToLogin,
            backgroundColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

