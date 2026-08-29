package com.example.basecamp.presentation.screens.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.BaseCampBackground
import com.example.basecamp.presentation.screens.shared.EventChatSection
import com.example.basecamp.presentation.screens.shared.EventChatViewModel

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

    val textPrimary = Color.White
    val textSecondary = Color(0xFFA0A0A0)
    val primaryAccent = Color(0xFFFF4B4B)
    val cardBackground = Color(0xFF1E1E1E)

    LaunchedEffect(dashboardState, eventId) {
        if (dashboardState is DashboardState.Success) {
            val eventsWithCounts = (dashboardState as DashboardState.Success).eventsWithCounts
            event = eventsWithCounts.find { it.first.id == eventId }?.first
        }
    }

    LaunchedEffect(eventId, selectedTab) {
        chatViewModel.loadComments(eventId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BaseCampBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                if (event == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryAccent)
                    }
                } else {
                    val validEvent = event!!
                    
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = textPrimary,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = primaryAccent,
                                    height = 4.dp
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Details", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) primaryAccent else textSecondary) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Chat", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) primaryAccent else textSecondary) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Volunteers", fontWeight = FontWeight.Bold, color = if (selectedTab == 2) primaryAccent else textSecondary) }
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
                                        color = textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "BY ${validEvent.orgName}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    DetailItem(label = "Description", value = validEvent.description, textPrimary, textSecondary)
                                    DetailItem(label = "DATE & TIME", value = validEvent.date, textPrimary, textSecondary)
                                    DetailItem(label = "Location", value = validEvent.location, textPrimary, textSecondary)
                                    DetailItem(label = "Cause", value = validEvent.cause, textPrimary, textSecondary)
                                    DetailItem(label = "Type of Work", value = validEvent.typeOfWork, textPrimary, textSecondary)
                                    DetailItem(label = "Payment", value = validEvent.payment, textPrimary, textSecondary)
                                    DetailItem(label = "Dress Code", value = validEvent.dressCode, textPrimary, textSecondary)
                                    DetailItem(label = "Contact", value = validEvent.contactDetails, textPrimary, textSecondary)
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(cardBackground)
                                            .padding(16.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "CAPACITY: ${validEvent.maxVolunteers} VOLUNTEERS",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = textPrimary
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Button(
                                                onClick = { showAddCapacityDialog = true },
                                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Increase Capacity", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(32.dp))
                                    
                                    Button(
                                        onClick = { showDeleteDialog = true },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Delete Event", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }
                            
                            // Dialogs ...
                            if (showAddCapacityDialog) {
                                AlertDialog(
                                    onDismissRequest = { showAddCapacityDialog = false },
                                    containerColor = cardBackground,
                                    title = { Text("Increase Capacity", fontWeight = FontWeight.ExtraBold, color = textPrimary) },
                                    text = {
                                        Column {
                                            Text("How many additional volunteers do you want to allow?", color = textSecondary)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            TextField(
                                                value = capacityToAdd,
                                                onValueChange = { capacityToAdd = it },
                                                placeholder = { Text("e.g. 5") }
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                val additional = capacityToAdd.toIntOrNull() ?: 0
                                                if (additional > 0) {
                                                    viewModel.addVolunteerSpots(eventId, validEvent.maxVolunteers, additional)
                                                    showAddCapacityDialog = false
                                                    onNavigateBack()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                                        ) {
                                            Text("Add", color = Color.White)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showAddCapacityDialog = false }) {
                                            Text("Cancel", color = textPrimary)
                                        }
                                    }
                                )
                            }

                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    containerColor = cardBackground,
                                    title = { Text("Delete Event", fontWeight = FontWeight.ExtraBold, color = textPrimary) },
                                    text = { Text("Are you sure you want to delete this event? This action cannot be undone.", color = textSecondary) },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                viewModel.deleteEvent(eventId)
                                                showDeleteDialog = false
                                                onNavigateBack()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                                        ) {
                                            Text("Delete", color = Color.White)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDeleteDialog = false }) {
                                            Text("Cancel", color = textPrimary)
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
                                            color = textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "Scan QR tickets at the event to mark volunteers as Attended.",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textSecondary,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(volunteers.size) { index ->
                                        val (user, ticket) = volunteers[index]
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(cardBackground)
                                                .padding(16.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = user.name,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 20.sp,
                                                    color = textPrimary
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                
                                                val statusColor = when (ticket.status) {
                                                    "Checked In" -> Color(0xFFFFC107) // Yellow
                                                    "Attended" -> Color(0xFF4CAF50) // Green
                                                    else -> textSecondary
                                                }
                                                
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(modifier = Modifier.size(12.dp).background(statusColor, CircleShape))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = ticket.status,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = textPrimary
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

@Composable
fun DetailItem(label: String, value: String, textPrimary: Color, textSecondary: Color) {
    if (value.isNotBlank()) {
        Column(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
            Text(
                text = label.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        }
    }
}
