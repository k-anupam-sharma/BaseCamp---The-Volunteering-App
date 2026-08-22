package com.example.basecamp.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.profileState.collectAsState()
    val isUpdated by viewModel.updateState.collectAsState()
    val isLoggedOut by viewModel.logoutState.collectAsState()
    val rsvpCount by viewModel.rsvpCount.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is ProfileState.Success) {
            val user = (state as ProfileState.Success).user
            name = user.name
            phone = user.phone ?: ""
            website = user.website ?: ""
        }
    }

    LaunchedEffect(isUpdated) {
        if (isUpdated) {
            viewModel.resetUpdateState()
            onNavigateBack()
        }
    }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            onLogout()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MY PROFILE",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        coil.compose.AsyncImage(
            model = "https://api.dicebear.com/9.x/bottts-neutral/png?seed=${viewModel.currentUserId}",
            contentDescription = "Large Profile Avatar",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (state is ProfileState.Loading) {
            CircularProgressIndicator(color = Color.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state is ProfileState.Success) {
            val user = (state as ProfileState.Success).user
            
            Text(
                text = "ROLE: ${user.role.uppercase()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EMAIL (LOCKED)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            BrutalistTextField(
                value = user.email,
                onValueChange = { },
                placeholder = "EMAIL"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "NAME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            BrutalistTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "NAME"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "PHONE NUMBER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            BrutalistTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "PHONE NUMBER"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "WEBSITE (OPTIONAL)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            BrutalistTextField(
                value = website,
                onValueChange = { website = it },
                placeholder = "WEBSITE"
            )

            Spacer(modifier = Modifier.height(48.dp))

            BrutalistButton(
                text = "SAVE CHANGES",
                onClick = { viewModel.updateProfile(name, phone, website) },
                modifier = Modifier.fillMaxWidth()
            )
            
        } else if (state is ProfileState.Error) {
            Text(
                text = (state as ProfileState.Error).message,
                color = Color(0xFFFF007F),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))
        Spacer(modifier = Modifier.height(32.dp))
        
        BrutalistButton(
            text = "LOGOUT",
            onClick = {
                viewModel.logout()
            },
            backgroundColor = Color.White,
            textColor = Color(0xFFFF007F), // Pink for logout
            modifier = Modifier.fillMaxWidth()
        )
    }
}
