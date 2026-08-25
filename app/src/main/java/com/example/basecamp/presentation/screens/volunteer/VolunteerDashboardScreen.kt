package com.example.basecamp.presentation.screens.volunteer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.basecamp.presentation.components.skeuoCard
import com.example.basecamp.presentation.components.skeuoIcon
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.GlacierButton
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.AnimatedBackground


@Composable
fun VolunteerDashboardScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToEventDetails: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    val rsvpState by viewModel.rsvpState.collectAsState()
    val rsvpEventIds by viewModel.rsvpEventIds.collectAsState()
    val attendedCount by viewModel.attendedCount.collectAsState()

    var selectedTab by remember { mutableStateOf("ALL") }

    val context = LocalContext.current

    LaunchedEffect(rsvpState) {
        when (rsvpState) {
            is RsvpState.Success -> {
                Toast.makeText(context, "Successfully RSVP'd!", Toast.LENGTH_SHORT).show()
                viewModel.resetRsvpState()
            }
            is RsvpState.Error -> {
                Toast.makeText(context, (rsvpState as RsvpState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetRsvpState()
            }
            else -> {}
        }
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(5_000)
            viewModel.fetchEvents(showLoading = false)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .skeuoCard(androidx.compose.foundation.shape.RoundedCornerShape(percent = 50))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconTint = Color(0xFFC5C5D4)
                    val selectedTint = Color.White
                    
                    androidx.compose.material3.IconButton(
                        onClick = { selectedTab = "ALL" },
                        modifier = Modifier
                            .size(48.dp)
                            .then(if (selectedTab == "ALL") Modifier.skeuoIcon(androidx.compose.foundation.shape.CircleShape) else Modifier)
                    ) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Filled.Explore, contentDescription = "All Events", tint = if (selectedTab == "ALL") selectedTint else iconTint)
                    }
                    
                    androidx.compose.material3.IconButton(
                        onClick = { selectedTab = "RSVPS" },
                        modifier = Modifier
                            .size(48.dp)
                            .then(if (selectedTab == "RSVPS") Modifier.skeuoIcon(androidx.compose.foundation.shape.CircleShape) else Modifier)
                    ) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Filled.EventAvailable, contentDescription = "My RSVPs", tint = if (selectedTab == "RSVPS") selectedTint else iconTint)
                    }

                    androidx.compose.material3.IconButton(
                        onClick = { selectedTab = "NOTIFICATIONS" },
                        modifier = Modifier
                            .size(48.dp)
                            .then(if (selectedTab == "NOTIFICATIONS") Modifier.skeuoIcon(androidx.compose.foundation.shape.CircleShape) else Modifier)
                    ) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Filled.Notifications, contentDescription = "Notifications", tint = if (selectedTab == "NOTIFICATIONS") selectedTint else iconTint)
                    }

                    androidx.compose.material3.IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.size(48.dp)
                    ) {
                        androidx.compose.material3.Icon(androidx.compose.material.icons.Icons.Filled.Person, contentDescription = "My Profile", tint = iconTint)
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
                text = "Volunteer Feed",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                letterSpacing = 1.sp
            )

        }
        
        // Attended Count Header
        GlassPanel(
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant, // Cyan
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "EVENTS ATTENDED: $attendedCount",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            }
        }

        when (feedState) {
            is FeedState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                }
            }
            is FeedState.Success -> {
                val availableEvents = (feedState as FeedState.Success).availableEvents
                val rsvpedEvents = (feedState as FeedState.Success).rsvpedEvents
                
                val eventsToShow = if (selectedTab == "ALL") availableEvents else rsvpedEvents
                
                if (eventsToShow.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (selectedTab == "ALL") "No available events" else "You haven't RSVP'd yet", 
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(eventsToShow) { event ->
                            val isRsvped = rsvpEventIds.contains(event.id)
                            EventCard(
                                event = event, 
                                isRsvped = isRsvped,
                                onRsvpClick = { 
                                    event.id?.let { viewModel.rsvpForEvent(it) } 
                                },
                                onShowQrClick = {
                                    if (isRsvped) {
                                        event.id?.let { onNavigateToEventDetails(it) }
                                    }
                                },
                                onCardClick = {
                                    event.id?.let { onNavigateToEventDetails(it) }
                                }
                            )
                        }
                    }
                }
            }
            is FeedState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = (feedState as FeedState.Error).message,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, // Hot Pink
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
}
}




@Composable
fun EventCard(event: Event, isRsvped: Boolean, onRsvpClick: () -> Unit, onShowQrClick: () -> Unit, onCardClick: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image Placeholder Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(androidx.compose.ui.graphics.Color(0xFFE0E0E0), androidx.compose.ui.graphics.Color(0xFFEEEEEE))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFFBDBDBD), modifier = Modifier.size(48.dp))
            }
            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(event.date.uppercase(), color = androidx.compose.ui.graphics.Color(0xFFD32F2F), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp) // Meetup red date
                Spacer(modifier = Modifier.height(4.dp))
                Text(event.title, fontSize = 18.sp, fontWeight = FontWeight.Black, color = androidx.compose.ui.graphics.Color.Black)
                Text("Hosted by " + event.orgName, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = androidx.compose.ui.graphics.Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.location, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isRsvped) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "You're going!",
                            color = androidx.compose.ui.graphics.Color(0xFF388E3C), // Green text
                            fontWeight = FontWeight.Bold
                        )
                        androidx.compose.material3.Button(
                            onClick = onShowQrClick,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFFF3F4F6), 
                                contentColor = androidx.compose.ui.graphics.Color.Black
                            )
                        ) {
                            Text("Show QR")
                        }
                    }
                } else {
                    androidx.compose.material3.Button(
                        onClick = onRsvpClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFFE53935), // Red button
                            contentColor = androidx.compose.ui.graphics.Color.White
                        )
                    ) {
                        Text("Attend", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


