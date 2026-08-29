package com.example.basecamp.presentation.screens.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.BaseCampBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrgDashboardScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToScan: () -> Unit,
    onNavigateToCreate: (String?) -> Unit,
    onNavigateToEventDetails: (String) -> Unit = {},
    viewModel: OrgViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    var selectedTab by remember { mutableStateOf("DASHBOARD") }

    val primaryAccent = Color(0xFFFF4B4B)
    val cardBackground = Color(0xFF1E1E1E)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFA0A0A0)

    LaunchedEffect(selectedTab) {
        // Handle tab changes if needed
    }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5_000)
            viewModel.fetchDashboardData(showLoading = false)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        BaseCampBackground {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .width(340.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color(0xFF161616)),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconTint = Color(0xFF707070)
                            val selectedTint = primaryAccent
                            
                            // 1. Dashboard
                            IconButton(onClick = { selectedTab = "DASHBOARD" }) {
                                Icon(
                                    Icons.Default.Dashboard,
                                    contentDescription = "Dashboard",
                                    tint = if (selectedTab == "DASHBOARD") selectedTint else iconTint
                                )
                            }
                            
                            // 2. Scan Tickets
                            IconButton(onClick = onNavigateToScan) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Tickets", tint = iconTint)
                            }

                            // 3. Create Event (Large +)
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(primaryAccent)
                                    .clickable { onNavigateToCreate(null) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Create Event", tint = Color.Black, modifier = Modifier.size(32.dp))
                            }

                            // 4. Notifications
                            IconButton(onClick = { selectedTab = "NOTIFICATIONS" }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (selectedTab == "NOTIFICATIONS") selectedTint else iconTint
                                )
                            }

                            // 5. My Profile
                            IconButton(onClick = onNavigateToProfile) {
                                Icon(Icons.Default.Person, contentDescription = "My Profile", tint = iconTint)
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Organization HQ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .clickable { onNavigateToProfile() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedTab == "NOTIFICATIONS") {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notifications", color = textSecondary)
                        }
                    } else {
                        when (dashboardState) {
                            is DashboardState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = primaryAccent)
                                }
                            }
                            is DashboardState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text((dashboardState as DashboardState.Error).message, color = primaryAccent)
                                }
                            }
                            is DashboardState.Success -> {
                                val eventsWithCounts = (dashboardState as DashboardState.Success).eventsWithCounts
                                if (eventsWithCounts.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No events yet. Tap the + to create one!", color = textSecondary)
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(eventsWithCounts) { (event, rsvpCount) ->
                                            NewOrgEventCard(
                                                event = event,
                                                rsvpCount = rsvpCount,
                                                onEditClick = { onNavigateToCreate(event.id ?: "") },
                                                onClick = { onNavigateToEventDetails(event.id ?: "") }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewOrgEventCard(
    event: Event,
    rsvpCount: Int,
    onEditClick: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = event.bannerUrl,
            contentDescription = "Event Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 100f
                    )
                )
        )
        
        // Date Badge Top Right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val parts = event.date.split(" ")
                if (parts.size >= 2) {
                    Text(parts[0], color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(parts[1], color = Color.White, fontSize = 10.sp)
                } else {
                    Text(event.date.take(5), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        // Bottom Content
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$rsvpCount/${event.maxVolunteers} RSVPs", color = Color(0xFFFF4B4B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = " • ", color = Color.LightGray, fontSize = 12.sp)
                    Text(text = event.cause, color = Color.LightGray, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Edit Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black, modifier = Modifier.size(20.dp))
            }
        }
    }
}
