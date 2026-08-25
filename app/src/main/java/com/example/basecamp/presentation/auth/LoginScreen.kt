package com.example.basecamp.presentation.auth

import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import com.example.basecamp.presentation.components.GlacierButton
import com.example.basecamp.presentation.components.AnimatedBackground
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.GlacierTextField

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    onNeedsProfileSetup: (String, String, String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val role = (authState as AuthState.Success).role
            onLoginSuccess(role) // or onSignupSuccess
        } else if (authState is AuthState.NeedsProfileSetup) {
            val state = authState as AuthState.NeedsProfileSetup
            onNeedsProfileSetup(state.userId, state.email, state.name)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
            .verticalScroll(rememberScrollState())
            
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("employee_content.lottie"))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(250.dp)
        )
        Text(
            text = "BaseCamp",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Log in to continue",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        GlacierTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email address"
        )

        Spacer(modifier = Modifier.height(16.dp))

        GlacierTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
        } else {
            

            GlacierButton(
                text = "Log In",
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GlacierButton(
                text = "Continue with Google",
                onClick = { viewModel.loginWithGoogle() },
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                textColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error, // Hot Pink for errors
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlacierButton(
            text = "Create Account",
            onClick = onNavigateToSignup,
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
    }
}




