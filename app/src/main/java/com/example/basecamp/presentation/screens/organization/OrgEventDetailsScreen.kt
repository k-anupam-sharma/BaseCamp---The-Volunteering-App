package com.example.basecamp.presentation.screens.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
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
import com.example.basecamp.presentation.screens.shared.EventChatSection
import com.example.basecamp.presentation.screens.shared.EventChatViewModel
import com.example.basecamp.presentation.screens.volunteer.DetailItem

@Composable
fun OrgEventDetailsScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: OrgViewModel = hiltViewModel(),
    chatViewModel: EventChatViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    var event by remember { mutableStateOf<Event?>(null) }
    
    var selectedTab by remember { mutableStateOf(0) }
    val chatState by chatViewModel.chatState.collectAsState()

    LaunchedEffect(dashboardState, eventId) {
        if (dashboardState is DashboardState.Success) {
            val eventsWithCounts = (dashboardState as DashboardState.Success).eventsWithCounts
            event = eventsWithCounts.find { it.first.id == eventId }?.first
        }
    }

    LaunchedEffect(eventId, selectedTab) {
        // Load chat if on first tab (since it's combined or just load it anyway)
        chatViewModel.loadComments(eventId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
    com.example.basecamp.presentation.components.AnimatedBackground()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            val validEvent = event!!
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF7DD3FC), // Hot Pink
                            height = 4.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Details", fontWeight = FontWeight.ExtraBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Chat", fontWeight = FontWeight.ExtraBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Volunteers", fontWeight = FontWeight.ExtraBold) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // DETAILS TAB
                    var showAddCapacityDialog by remember { mutableStateOf(false) }
                    var capacityToAdd by remember { mutableStateOf("") }
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = validEvent.title,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "BY ${validEvent.orgName}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            DetailItem(label = "Description", value = validEvent.description)
                            DetailItem(label = "DATE & TIME", value = validEvent.date)
                            DetailItem(label = "Location", value = validEvent.location)
                            DetailItem(label = "Cause", value = validEvent.cause)
                            DetailItem(label = "Type of Work", value = validEvent.typeOfWork)
                            DetailItem(label = "Payment", value = validEvent.payment)
                            DetailItem(label = "Dress Code", value = validEvent.dressCode)
                            DetailItem(label = "Contact", value = validEvent.contactDetails)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            GlassPanel {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "CAPACITY: ${validEvent.maxVolunteers} VOLUNTEERS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    GlacierButton(
                                        text = "Increase Capacity",
                                        onClick = { showAddCapacityDialog = true }, // Electric Yellow
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            GlacierButton(
                                text = "Delete Event",
                                onClick = { showDeleteDialog = true },
                                backgroundColor = Color(0xFF7DD3FC), // Hot Pink
                                textColor = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                    
                    if (showAddCapacityDialog) {
                        AlertDialog(
                            onDismissRequest = { showAddCapacityDialog = false },
                            title = { Text("Increase Capacity", fontWeight = FontWeight.ExtraBold) },
                            text = {
                                Column {
                                    Text("How many additional volunteers do you want to allow?")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextField(
                                        value = capacityToAdd,
                                        onValueChange = { capacityToAdd = it },
                                        placeholder = { Text("e.g. 5") }
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    val additional = capacityToAdd.toIntOrNull() ?: 0
                                    if (additional > 0) {
                                        viewModel.addVolunteerSpots(eventId, validEvent.maxVolunteers, additional)
                                        showAddCapacityDialog = false
                                        onNavigateBack() // Go back after updating (or refresh)
                                    }
                                }) {
                                    Text("Add")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddCapacityDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete Event", fontWeight = FontWeight.ExtraBold) },
                            text = { Text("Are you sure you want to delete this event? This action cannot be undone.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.deleteEvent(eventId)
                                        showDeleteDialog = false
                                        onNavigateBack()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC))
                                ) {
                                    Text("Delete", color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                                }
                            }
                        )
                    }
                }
                1 -> {
                    // CHAT TAB
                    EventChatSection(
                        chatState = chatState,
                        eventOwnerId = validEvent.orgId,
                        onAddComment = { text, parentId ->
                            chatViewModel.addComment(eventId, text, parentId)
                        },
                        onToggleLike = { commentId ->
                            chatViewModel.toggleLike(commentId)
                        },
                        onRefresh = {
                            chatViewModel.loadComments(eventId)
                        }
                    )
                }
                2 -> {
                    // VOLUNTEERS TAB
                    val volunteers by viewModel.eventVolunteersState.collectAsState()
                    
                    LaunchedEffect(eventId) {
                        viewModel.fetchEventVolunteers(eventId)
                        while (true) {
                            kotlinx.coroutines.delay(10_000)
                            viewModel.fetchEventVolunteers(eventId)
                        }
                    }

                    if (volunteers.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No volunteers yet",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Scan QR tickets at the event to mark volunteers as Attended.",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(volunteers) { (user, ticket) ->
                                GlassPanel( modifier = Modifier.padding(vertical = 8.dp)) {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                        Text(
                                            text = user.name,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        val statusColor = when (ticket.status) {
                                            "Checked In" -> Color(0xFFFAFF00) // Yellow
                                            "Attended" -> Color(0xFF00FF00) // Green
                                            else -> Color.LightGray
                                        }
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(12.dp).background(statusColor))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = ticket.status,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        
                                        // Calculate duration if attended
                                        if (ticket.status == "Attended" && ticket.checkInTime != null && ticket.checkOutTime != null) {
                                            var durationStr: String? = null
                                            try {
                                                val checkIn = java.time.Instant.parse(ticket.checkInTime)
                                                val checkOut = java.time.Instant.parse(ticket.checkOutTime)
                                                val durationMinutes = java.time.Duration.between(checkIn, checkOut).toMinutes()
                                                val hours = durationMinutes / 60
                                                val mins = durationMinutes % 60
                                                durationStr = "${hours}h ${mins}m"
                                            } catch (e: Exception) {
                                                // Ignore parse errors
                                            }
                                            
                                            if (durationStr != null) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = "DURATION: $durationStr",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF7DD3FC) // Hot Pink
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
}
}
