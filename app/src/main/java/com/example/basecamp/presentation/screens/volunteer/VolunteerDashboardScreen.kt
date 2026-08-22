package com.example.basecamp.presentation.screens.volunteer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistCard

@Composable
fun VolunteerDashboardScreen(
    onNavigateToProfile: () -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    val rsvpState by viewModel.rsvpState.collectAsState()
    val rsvpEventIds by viewModel.rsvpEventIds.collectAsState()
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

    var selectedTab by remember { mutableStateOf("ALL") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BrutalistButton(
                    text = "ALL EVENTS",
                    onClick = { selectedTab = "ALL" },
                    backgroundColor = if (selectedTab == "ALL") Color(0xFFFAFF00) else Color.White,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                BrutalistButton(
                    text = "MY RSVPs",
                    onClick = { selectedTab = "RSVPS" },
                    backgroundColor = if (selectedTab == "RSVPS") Color(0xFFFAFF00) else Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F0))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EVENT FEED",
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

        when (feedState) {
            is FeedState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
            is FeedState.Success -> {
                val availableEvents = (feedState as FeedState.Success).availableEvents
                val rsvpedEvents = (feedState as FeedState.Success).rsvpedEvents
                
                val eventsToShow = if (selectedTab == "ALL") availableEvents else rsvpedEvents
                
                if (eventsToShow.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (selectedTab == "ALL") "NO AVAILABLE EVENTS" else "YOU HAVEN'T RSVP'D YET", 
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
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
                        color = Color(0xFFFF007F), // Hot Pink
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
}

@Composable
fun EventCard(event: Event, isRsvped: Boolean, onRsvpClick: () -> Unit) {
    BrutalistCard(
        modifier = Modifier.fillMaxWidth(),
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
                text = "ORG: ${event.orgName}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
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
            
            if (isRsvped) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RSVP'D",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.DarkGray
                    )
                }
            } else {
                BrutalistButton(
                    text = "RSVP",
                    onClick = onRsvpClick,
                    backgroundColor = Color(0xFFFAFF00), // Electric Yellow
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


