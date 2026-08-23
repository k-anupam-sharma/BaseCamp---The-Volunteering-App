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
import com.example.basecamp.presentation.components.BrutalistCard
import com.example.basecamp.presentation.components.BrutalistButton
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0))
            .padding(16.dp)
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            val validEvent = event!!
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFF007F), // Hot Pink
                            height = 4.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("DETAILS", fontWeight = FontWeight.ExtraBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("CHAT", fontWeight = FontWeight.ExtraBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("VOLUNTEERS", fontWeight = FontWeight.ExtraBold) }
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
                                text = validEvent.title.uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "BY ${validEvent.orgName.uppercase()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            DetailItem(label = "DESCRIPTION", value = validEvent.description)
                            DetailItem(label = "DATE & TIME", value = validEvent.date)
                            DetailItem(label = "LOCATION", value = validEvent.location)
                            DetailItem(label = "CAUSE", value = validEvent.cause)
                            DetailItem(label = "TYPE OF WORK", value = validEvent.typeOfWork)
                            DetailItem(label = "PAYMENT", value = validEvent.payment)
                            DetailItem(label = "DRESS CODE", value = validEvent.dressCode)
                            DetailItem(label = "CONTACT", value = validEvent.contactDetails)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            BrutalistCard(backgroundColor = Color.White) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "CAPACITY: ${validEvent.maxVolunteers} VOLUNTEERS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BrutalistButton(
                                        text = "INCREASE CAPACITY",
                                        onClick = { showAddCapacityDialog = true },
                                        backgroundColor = Color(0xFFFAFF00), // Electric Yellow
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            BrutalistButton(
                                text = "DELETE EVENT",
                                onClick = { showDeleteDialog = true },
                                backgroundColor = Color(0xFFFF007F), // Hot Pink
                                textColor = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                    
                    if (showAddCapacityDialog) {
                        AlertDialog(
                            onDismissRequest = { showAddCapacityDialog = false },
                            title = { Text("INCREASE CAPACITY", fontWeight = FontWeight.ExtraBold) },
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
                                    Text("ADD")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddCapacityDialog = false }) {
                                    Text("CANCEL")
                                }
                            }
                        )
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("DELETE EVENT", fontWeight = FontWeight.ExtraBold) },
                            text = { Text("Are you sure you want to delete this event? This action cannot be undone.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.deleteEvent(eventId)
                                        showDeleteDialog = false
                                        onNavigateBack()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F))
                                ) {
                                    Text("DELETE", color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("CANCEL", color = Color.Black)
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
                                    text = "NO VOLUNTEERS YET",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
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
                                BrutalistCard(backgroundColor = Color.White, modifier = Modifier.padding(vertical = 8.dp)) {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                        Text(
                                            text = user.name.uppercase(),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp,
                                            color = Color.Black
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
                                                text = ticket.status.uppercase(),
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
                                                    color = Color(0xFFFF007F) // Hot Pink
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
