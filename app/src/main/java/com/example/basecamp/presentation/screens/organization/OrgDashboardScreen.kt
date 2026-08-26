package com.example.basecamp.presentation.screens.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.GlacierButton
import com.example.basecamp.presentation.components.AnimatedBackground
import com.example.basecamp.presentation.components.skeuoCard
import com.example.basecamp.presentation.components.skeuoIcon
import com.example.basecamp.presentation.components.skeuoButtonPrimary

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

    androidx.compose.runtime.LaunchedEffect(selectedTab) {
        if (selectedTab == "NOTIFICATIONS") {
            viewModel.fetchNotifications()
        }
    }

        LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5_000)
            viewModel.fetchDashboardData(showLoading = false)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .width(330.dp)
                            .skeuoCard(RoundedCornerShape(percent = 50))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconTint = Color(0xFFC5C5D4)
                        val selectedTint = Color.White
                        
                        // 1. Dashboard
                        IconButton(
                            onClick = { selectedTab = "DASHBOARD" },
                            modifier = Modifier
                                .size(48.dp)
                                .then(if (selectedTab == "DASHBOARD") Modifier.skeuoIcon(CircleShape) else Modifier)
                        ) {
                            Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard", tint = if (selectedTab == "DASHBOARD") selectedTint else iconTint)
                        }
                        
                        // 2. Scan Tickets
                        IconButton(
                            onClick = onNavigateToScan,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan Tickets", tint = iconTint)
                        }

                        // 3. Create Event (Large +)
                        IconButton(
                            onClick = { onNavigateToCreate(null) },
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(
                                    elevation = 6.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0x80000000),
                                    ambientColor = Color(0x80000000)
                                )
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Create Event", tint = Color.Black, modifier = Modifier.size(32.dp))
                        }

                        // 4. Notifications
                        IconButton(
                            onClick = { selectedTab = "NOTIFICATIONS" },
                            modifier = Modifier
                                .size(48.dp)
                                .then(if (selectedTab == "NOTIFICATIONS") Modifier.skeuoIcon(CircleShape) else Modifier)
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = if (selectedTab == "NOTIFICATIONS") selectedTint else iconTint)
                        }

                        // 5. My Profile
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "My Profile", tint = iconTint)
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Org Dashboard",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 1.sp
                    )
                }
                
                
                
                when (dashboardState) {
                    is DashboardState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    is DashboardState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = (dashboardState as DashboardState.Error).message,
                                color = Color(0xFF7DD3FC),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    is DashboardState.Success -> {
                        if (selectedTab == "NOTIFICATIONS") {
                            val notifs by viewModel.notifications.collectAsState()
                            if (notifs.isEmpty()) {
                                androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    androidx.compose.material3.Text("No notifications", fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                                }
                            } else {
                                androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    items(notifs.size) { index ->
                                        val notif = notifs[index]
                                        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth().skeuoCard(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)).padding(16.dp)) {
                                            androidx.compose.foundation.layout.Column {
                                                androidx.compose.material3.Text(notif.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                                                androidx.compose.material3.Text(notif.message, color = Color.LightGray)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val eventsWithCounts = (dashboardState as DashboardState.Success).eventsWithCounts
                            if (eventsWithCounts.isEmpty()) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.material3.Text(text = "No active events", fontWeight = FontWeight.Bold, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                                }
                            } else {
                                androidx.compose.foundation.lazy.LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(eventsWithCounts) { (event, count) ->
                                        OrgEventCard(
                                              event = event,
                                              count = count,
                                              viewModel = viewModel,
                                              onNavigateToEventDetails = onNavigateToEventDetails,
                                              onEditEvent = { onNavigateToCreate(it) }
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

@Composable
fun OrgEventCard(
    event: Event,
    onEditEvent: (String) -> Unit,
    count: Int,
    viewModel: OrgViewModel,
    onNavigateToEventDetails: (String) -> Unit
) {
    com.example.basecamp.presentation.components.GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { event.id?.let { onNavigateToEventDetails(it) } },
        backgroundColor = androidx.compose.ui.graphics.Color(0x662B2B2B)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            if (event.bannerUrl != null) {
                coil.compose.AsyncImage(
                    model = event.bannerUrl,
                    contentDescription = "Event Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            val displayDate = if (event.isMultiDay && event.endDate.isNotBlank()) {
                "${event.date} - ${event.endDate}".uppercase()
            } else {
                event.date.uppercase()
            }
            Text(
                text = displayDate,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFFD4D4D4)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = androidx.compose.ui.graphics.Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = androidx.compose.ui.graphics.Color(0xFFB3B3B3), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.location,
                    fontSize = 12.sp,
                    color = androidx.compose.ui.graphics.Color(0xFFB3B3B3)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            val maxStr = if (event.maxVolunteers > 0) event.maxVolunteers.toString() else "Unlimited"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Group, contentDescription = "RSVPs", tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${count} / ${maxStr} RSVPs",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    androidx.compose.material3.IconButton(
                        onClick = { event.id?.let { onEditEvent(it) } },
                        modifier = Modifier.size(36.dp).background(androidx.compose.ui.graphics.Color(0x40FFFFFF), CircleShape)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Event", tint = androidx.compose.ui.graphics.Color.White)
                    }
                    androidx.compose.material3.IconButton(
                        onClick = { event.id?.let { viewModel.deleteEvent(it) } },
                        modifier = Modifier.size(36.dp).background(Color(0xFFE53935).copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFFFCDD2))
                    }
                }
            }
        }
    }
}





