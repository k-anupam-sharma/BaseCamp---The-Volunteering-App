package com.example.basecamp.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.basecamp.presentation.components.AnimatedBackground
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.presentation.components.GlacierButton
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.GlacierTextField

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
    
    var showPhotoPickerMenu by remember { mutableStateOf(false) }
    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val isUploadingPhoto by viewModel.isUploadingPhoto.collectAsState()
    val uploadError by viewModel.uploadError.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uploadError) {
        uploadError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUploadError()
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
    AnimatedBackground()
    Column(
        modifier = Modifier
            .fillMaxSize()
            
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
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Profile",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                cameraUri?.let { viewModel.uploadProfilePhoto(it, context) }
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: android.net.Uri? ->
            uri?.let { viewModel.uploadProfilePhoto(it, context) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val avatarUrl = if (state is ProfileState.Success) {
            (state as ProfileState.Success).user.profileImageUrl
        } else null

        val displayUrl = avatarUrl ?: "https://api.dicebear.com/9.x/bottts-neutral/png?seed=${viewModel.currentUserId}"

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            coil.compose.AsyncImage(
                model = displayUrl,
                contentDescription = "Large Profile Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .then(
                        if (!isUploadingPhoto) Modifier.clickable { showPhotoPickerMenu = true }
                        else Modifier
                    )
            )
            
            if (isUploadingPhoto) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { showPhotoPickerMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Profile Picture",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (showPhotoPickerMenu) {
            Dialog(onDismissRequest = { showPhotoPickerMenu = false }) {
                GlassPanel {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Profile Picture",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        GlacierButton(
                            text = "Take Photo",
                            onClick = {
                                showPhotoPickerMenu = false
                                val uri = viewModel.createTempFileUri(context)
                                cameraUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GlacierButton(
                            text = "Choose from Gallery",
                            onClick = {
                                showPhotoPickerMenu = false
                                launcher.launch("image/*")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state is ProfileState.Loading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state is ProfileState.Success) {
            val user = (state as ProfileState.Success).user
            
            Text(
                text = "ROLE: ${user.role}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EMAIL (LOCKED)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            GlacierTextField(
                value = user.email,
                onValueChange = { },
                placeholder = "Email"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Name",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            GlacierTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Name"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Phone Number",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            GlacierTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "Phone number"
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "WEBSITE (OPTIONAL)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            GlacierTextField(
                value = website,
                onValueChange = { website = it },
                placeholder = "Website"
            )

            if (user.role.equals("Volunteer", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "My Badges",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GAMIFICATION_BADGES.forEach { badge ->
                        val isUnlocked = rsvpCount >= badge.requiredRsvps
                        val bgColor = if (isUnlocked) Color(badge.color) else Color(0x662B2B2B)
                        val textColor = if (isUnlocked) Color.Black else Color.LightGray

                            GlassPanel(
                            modifier = Modifier.weight(1f).height(140.dp),
                            backgroundColor = bgColor
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                    contentDescription = if (isUnlocked) "Unlocked" else "Locked",
                                    tint = textColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = badge.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = textColor,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${badge.requiredRsvps} RSVPs",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            GlacierButton(
                text = "Save Changes",
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
        
        GlacierButton(
            text = "Log Out",
            onClick = {
                viewModel.logout()
            },
            backgroundColor = Color.White,
            textColor = Color(0xFFFF007F), // Pink for logout
            modifier = Modifier.fillMaxWidth()
        )
    }
}
}


