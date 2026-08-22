package com.example.basecamp.presentation.screens.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.presentation.theme.brutalistStyle
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.BrutalistCard
import com.example.basecamp.presentation.components.BrutalistButton

@Composable
fun OrgDashboardScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToScan: () -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: OrgViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF4F4F0), // Off-white background
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .brutalistStyle(cornerRadius = 0.dp)
                        .background(Color.White)
                        .clickable(onClick = onNavigateToCreate)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("CREATE EVENT", color = Color.Black, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .brutalistStyle(cornerRadius = 0.dp)
                        .background(Color(0xFFFAFF00)) // Electric Yellow
                        .clickable(onClick = onNavigateToScan)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SCAN TICKETS",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORG DASHBOARD",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onNavigateToProfile) {
                    coil.compose.AsyncImage(
                        model = "https://api.dicebear.com/9.x/bottts-neutral/png?seed=${viewModel.currentUserId}",
                        contentDescription = "Profile Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
            }
            
            when (dashboardState) {
                is DashboardState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }
                is DashboardState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = (dashboardState as DashboardState.Error).message,
                            color = Color(0xFFFF007F), // Hot Pink
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                is DashboardState.Success -> {
                    val eventsWithCounts = (dashboardState as DashboardState.Success).eventsWithCounts
                    if (eventsWithCounts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "NO ACTIVE EVENTS", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(eventsWithCounts) { (event, count) ->
                                OrgEventCard(
                                    event = event, 
                                    rsvpCount = count,
                                    onDeleteClick = { event.id?.let { viewModel.deleteEvent(it) } },
                                    onAddSpotsClick = { amount -> event.id?.let { viewModel.addVolunteerSpots(it, event.maxVolunteers, amount) } }
                                )
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
    rsvpCount: Int, 
    onDeleteClick: () -> Unit, 
    onAddSpotsClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    BrutalistCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = event.title.uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CAUSE: ${event.cause}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF007F) // Hot Pink for cause tags
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📍 ${event.location} | 🗓 ${event.date}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val maxStr = if (event.maxVolunteers > 0) event.maxVolunteers.toString() else "∞"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFF00)) // Electric Yellow
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$rsvpCount / $maxStr RSVPs",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BrutalistButton(
                        text = "DELETE",
                        onClick = onDeleteClick,
                        backgroundColor = Color(0xFFFF007F), // Hot Pink
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BrutalistButton(
                        text = "+1 SPOT",
                        onClick = { onAddSpotsClick(1) },
                        backgroundColor = Color(0xFFFAFF00), // Electric Yellow
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BrutalistButton(
                        text = "+5 SPOTS",
                        onClick = { onAddSpotsClick(5) },
                        backgroundColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "▼ TAP TO MANAGE EVENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


