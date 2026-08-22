package com.example.basecamp.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistTextField


@Composable
fun CompleteProfileScreen(
    userId: String,
    initialEmail: String,
    initialName: String,
    onSetupSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var role by remember { mutableStateOf("Volunteer") }
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSetupSuccess((authState as AuthState.Success).role)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ALMOST THERE",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "COMPLETE YOUR PROFILE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
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
            val volColor = if (role == "Volunteer") Color(0xFFFAFF00) else Color.White
            val orgColor = if (role == "Organization") Color(0xFFFAFF00) else Color.White

            BrutalistButton(
                text = "VOLUNTEER",
                onClick = { role = "Volunteer" },
                backgroundColor = volColor,
                modifier = Modifier.weight(1f)
            )
            BrutalistButton(
                text = "ORGANIZATION",
                onClick = { role = "Organization" },
                backgroundColor = orgColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (role == "Volunteer") {
            BrutalistTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "FULL NAME"
            )
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
                placeholder = "PHONE NUMBER"
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrutalistTextField(
                value = website,
                onValueChange = { website = it },
                placeholder = "WEBSITE (OPTIONAL)"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = Color.Black)
        } else {
            BrutalistButton(
                text = "FINISH ACCOUNT CREATION",
                onClick = {
                    viewModel.completeGoogleSignup(
                        userId = userId,
                        email = initialEmail,
                        name = name,
                        role = role,
                                                phone = phone,
                        website = website
                    )
                },
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
    }
}
