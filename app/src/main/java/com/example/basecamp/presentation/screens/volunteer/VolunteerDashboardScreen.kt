package com.example.basecamp.presentation.screens.volunteer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
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
fun VolunteerDashboardScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToEventDetails: (String) -> Unit,
    onNavigateToTicket: (String) -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    val rsvpState by viewModel.rsvpState.collectAsState()
    val rsvpEventIds by viewModel.rsvpEventIds.collectAsState()

    var selectedTab by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCause by remember { mutableStateOf("All") }

    val context = LocalContext.current

    val primaryAccent = Color(0xFFFF4B4B)
    val cardBackground = Color(0xFF1E1E1E)
    val searchBackground = Color(0xFF2C2C2C)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFA0A0A0)

    val causes = listOf("All", "Animal Welfare", "Environmental", "Education", "Health", "Community", "Other")

    LaunchedEffect(selectedTab) {
        // Handle tab changes if needed
    }

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
                                .width(300.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color(0xFF161616)),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconTint = Color(0xFF707070)
                            val selectedTint = primaryAccent

                            IconButton(onClick = { selectedTab = "ALL" }) {
                                Icon(
                                    Icons.Default.Explore,
                                    contentDescription = "All Events",
                                    tint = if (selectedTab == "ALL") selectedTint else iconTint
                                )
                            }
                            IconButton(onClick = { selectedTab = "RSVPS" }) {
                                Icon(
                                    Icons.Default.EventAvailable,
                                    contentDescription = "My RSVPs",
                                    tint = if (selectedTab == "RSVPS") selectedTint else iconTint
                                )
                            }
                            IconButton(onClick = { selectedTab = "NOTIFICATIONS" }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (selectedTab == "NOTIFICATIONS") selectedTint else iconTint
                                )
                            }
                            IconButton(onClick = onNavigateToProfile) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "My Profile",
                                    tint = iconTint
                                )
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
                        IconButton(onClick = { /* Menu */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textPrimary)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = primaryAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Global", color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .clickable { onNavigateToProfile() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Search Bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = searchBackground,
                            unfocusedContainerColor = searchBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            cursorColor = primaryAccent
                        ),
                        placeholder = { Text("Search all events...", color = textSecondary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = textSecondary) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Upcoming events", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Cause Filters
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(causes) { cause ->
                            val isSelected = selectedCause == cause
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isSelected) primaryAccent else searchBackground)
                                    .clickable { selectedCause = cause }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cause,
                                    color = if (isSelected) Color.White else textSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Content
                    if (selectedTab == "NOTIFICATIONS") {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notifications", color = textSecondary)
                        }
                    } else {
                        when (feedState) {
                            is FeedState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = primaryAccent)
                                }
                            }
                            is FeedState.Error -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text((feedState as FeedState.Error).message, color = primaryAccent)
                                }
                            }
                            is FeedState.Success -> {
                                val allEvents = (feedState as FeedState.Success).availableEvents + (feedState as FeedState.Success).rsvpedEvents
                                val filteredEvents = allEvents.filter { event ->
                                    val safeId = event.id ?: ""
                                    val matchesSearch = event.title.contains(searchQuery, ignoreCase = true) || event.location.contains(searchQuery, ignoreCase = true)
                                    val matchesCause = selectedCause == "All" || event.cause == selectedCause
                                    val matchesTab = if (selectedTab == "RSVPS") rsvpEventIds.contains(safeId) else !rsvpEventIds.contains(safeId)
                                    matchesSearch && matchesCause && matchesTab
                                }

                                if (filteredEvents.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("No events found", color = textSecondary)
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(filteredEvents) { event ->
                                            val safeId = event.id ?: ""
                                            val isRsvped = rsvpEventIds.contains(safeId)
                                            NewEventCard(
                                                event = event,
                                                isRsvped = isRsvped,
                                                onClick = {
                                                    if (isRsvped) {
                                                        onNavigateToTicket(safeId)
                                                    } else {
                                                        onNavigateToEventDetails(safeId)
                                                    }
                                                },
                                                onRsvpClick = { viewModel.rsvpForEvent(safeId) }
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
fun NewEventCard(
    event: Event,
    isRsvped: Boolean,
    onClick: () -> Unit,
    onRsvpClick: () -> Unit
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
                // Parsing logic to split "10 Feb 2026" or similar
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
                    Text(text = event.location, color = Color.LightGray, fontSize = 12.sp)
                    Text(text = " • ", color = Color.LightGray, fontSize = 12.sp)
                    Text(text = event.cause, color = Color.LightGray, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Price/Free Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRsvped) "View Ticket" else "View Details",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
