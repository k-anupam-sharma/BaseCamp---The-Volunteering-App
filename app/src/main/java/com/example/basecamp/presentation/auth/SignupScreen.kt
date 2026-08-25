package com.example.basecamp.presentation.auth

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
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: (String) -> Unit,
    onNeedsProfileSetup: (String, String, String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Volunteer") } // "Volunteer" or "Organization"

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val role = (authState as AuthState.Success).role
            onSignupSuccess(role) // or onSignupSuccess
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
            .background(Color(0xFFF4F4F0))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Join BaseCamp",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Role Selection
        Text(
            text = "SELECT YOUR ROLE:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val volColor = if (selectedRole == "Volunteer") Color(0xFFFAFF00) else Color.White
            val orgColor = if (selectedRole == "Organization") Color(0xFFFAFF00) else Color.White

            GlacierButton(
                text = "Volunteer",
                onClick = { selectedRole = "Volunteer" },
                backgroundColor = volColor,
                modifier = Modifier.weight(1f)
            )
            GlacierButton(
                text = "Organization",
                onClick = { selectedRole = "Organization" },
                backgroundColor = orgColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (selectedRole == "Volunteer") {
            GlacierTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Full name"
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            GlacierTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Organization name"
            )
            Spacer(modifier = Modifier.height(16.dp))
            GlacierTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "PHONE NUMBER (OPTIONAL)"
            )
            Spacer(modifier = Modifier.height(16.dp))
            GlacierTextField(
                value = website,
                onValueChange = { website = it },
                placeholder = "WEBSITE (OPTIONAL)"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                text = "Sign Up",
                onClick = { viewModel.signup(name, email, password, selectedRole, phone.takeIf { selectedRole == "Organization" }, website.takeIf { selectedRole == "Organization" }) },
                backgroundColor = Color(0xFF00E5FF), // Bright Cyan for primary action
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
                color = androidx.compose.material3.MaterialTheme.colorScheme.error, // Hot Pink
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        GlacierButton(
            text = "Back to Login",
            onClick = onNavigateToLogin,
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
    }
}



