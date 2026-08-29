package com.example.basecamp.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Button
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import com.example.basecamp.presentation.components.BaseCampBackground

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
    var selectedRole by remember { mutableStateOf("Volunteer") } 
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val role = (authState as AuthState.Success).role
            onSignupSuccess(role) 
        } else if (authState is AuthState.NeedsProfileSetup) {
            val state = authState as AuthState.NeedsProfileSetup
            onNeedsProfileSetup(state.userId, state.email, state.name)
        }
    }

    val primaryAccent = Color(0xFFF3B984)
    val cardBackground = Color(0xFF1A1A1E)
    val inputBackground = Color(0xFF24242A)
    val bgColor = Color(0xFF101014)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFA0A0A0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // The floating card
            Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 32.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(cardBackground)
                        .padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo Placeholder (Hexagon like image)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A2A2C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hexagon, contentDescription = "Logo", tint = primaryAccent, modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Sign Up",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create an account to continue",
                            fontSize = 14.sp,
                            color = textSecondary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Role Selection
                        Text(
                            text = "Select Role",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val volColor = if (selectedRole == "Volunteer") primaryAccent else inputBackground
                            val volTextColor = if (selectedRole == "Volunteer") Color.White else textSecondary
                            val orgColor = if (selectedRole == "Organization") primaryAccent else inputBackground
                            val orgTextColor = if (selectedRole == "Organization") Color.White else textSecondary

                            Button(
                                onClick = { selectedRole = "Volunteer" },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = volColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Volunteer", color = volTextColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = { selectedRole = "Organization" },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = orgColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Organization", color = orgTextColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Custom Input Field Helper
                        @Composable
                        fun CustomInput(
                            label: String,
                            value: String,
                            onValueChange: (String) -> Unit,
                            placeholder: String,
                            icon: androidx.compose.ui.graphics.vector.ImageVector,
                            isPassword: Boolean = false
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                androidx.compose.material3.TextField(
                                    value = value,
                                    onValueChange = onValueChange,
                                    placeholder = { Text(placeholder, color = textSecondary, fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(icon, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                                    },
                                    trailingIcon = if (isPassword) {
                                        {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = "Toggle Password",
                                                    tint = textSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    } else null,
                                    visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                                        focusedContainerColor = inputBackground,
                                        unfocusedContainerColor = inputBackground,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        if (selectedRole == "Volunteer") {
                            CustomInput("Full Name", name, { name = it }, "John Doe", Icons.Default.Person)
                        } else {
                            CustomInput("Organization Name", name, { name = it }, "BaseCamp Org", Icons.Default.Business)
                            CustomInput("Phone Number (Optional)", phone, { phone = it }, "+1 234 567 890", Icons.Default.Phone)
                        }

                        CustomInput("Email Address", email, { email = it }, "you@example.com", Icons.Default.Email)
                        CustomInput("Password", password, { password = it }, "••••••••", Icons.Default.Lock, isPassword = true)

                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(color = primaryAccent)
                        } else {
                            Button(
                                onClick = { viewModel.signup(name, email, password, selectedRole, phone.takeIf { selectedRole == "Organization" }, website.takeIf { selectedRole == "Organization" }) },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = primaryAccent),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Sign Up", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        if (authState is AuthState.Error) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = Color(0xFFFF4B4B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(modifier = Modifier.weight(1f), color = textSecondary.copy(alpha = 0.3f))
                            Text("or continue with", color = textSecondary, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
                            Divider(modifier = Modifier.weight(1f), color = textSecondary.copy(alpha = 0.3f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Social Row
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24242A))
                                    .clickable { viewModel.loginWithGoogle() },
                                contentAlignment = Alignment.Center
                            ) {
                                // Google 'G' text as a fallback since no drawable
                                Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24242A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhoneIphone, contentDescription = "Apple", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24242A)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Using a generic icon for GitHub placeholder
                                Icon(Icons.Default.Code, contentDescription = "GitHub", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Already have an account? ", color = textSecondary, fontSize = 14.sp)
                            Text(
                                "Log in",
                                color = primaryAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNavigateToLogin() }
                            )
                        }
                    }
                }
                
                // Footer text outside card
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = textSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your data is secure with us", color = textSecondary, fontSize = 12.sp)
                }
            }
        }
    }



