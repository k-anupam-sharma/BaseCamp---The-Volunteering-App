package com.example.basecamp.presentation.screens.volunteer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.AnimatedBackground
import com.example.basecamp.utils.QrCodeGenerator
import org.json.JSONObject
import com.example.basecamp.presentation.screens.shared.EventChatSection
import com.example.basecamp.presentation.screens.shared.EventChatViewModel
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun EventDetailsScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
    chatViewModel: EventChatViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    var event by remember { mutableStateOf<Event?>(null) }
    
    val currentUserId = viewModel.currentUserId

    var selectedTab by remember { mutableStateOf(0) }
    val chatState by chatViewModel.chatState.collectAsState()

    LaunchedEffect(feedState, eventId) {
        if (feedState is FeedState.Success) {
            val allEvents = (feedState as FeedState.Success).availableEvents + (feedState as FeedState.Success).rsvpedEvents
            event = allEvents.find { it.id == eventId }
        }
    }

    LaunchedEffect(eventId, selectedTab) {
        if (selectedTab == 1) {
            chatViewModel.loadComments(eventId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    com.example.basecamp.presentation.components.AnimatedBackground()
    Column(
        modifier = Modifier
            .fillMaxSize()
            
            .padding(16.dp)
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            }
        } else {
            val validEvent = event!!
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, // Hot Pink
                            height = 4.dp
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("TICKET & DETAILS", fontWeight = FontWeight.ExtraBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Chat", fontWeight = FontWeight.ExtraBold) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    // Top section: QR Code Ticket
                    GlassPanel(
                        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Ticket",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val payload = JSONObject().apply {
                                put("eventId", validEvent.id)
                                put("volunteerId", currentUserId)
                            }.toString()
                            
                            val qrBitmap = remember(payload) {
                                QrCodeGenerator.generateQrCode(payload)
                            }
                            
                            val myTickets by viewModel.myTickets.collectAsState()
                            val myTicket = myTickets.find { it.eventId == eventId }

                            var isQrRevealed by remember { mutableStateOf(false) }
                            var qrTimeRemaining by remember { mutableStateOf(15) }

                            LaunchedEffect(isQrRevealed) {
                                if (isQrRevealed) {
                                    qrTimeRemaining = 15
                                    while (qrTimeRemaining > 0) {
                                        kotlinx.coroutines.delay(1000)
                                        qrTimeRemaining -= 1
                                        if (qrTimeRemaining % 3 == 0) {
                                            viewModel.fetchEvents(showLoading = false)
                                        }
                                    }
                                    isQrRevealed = false
                                }
                            }

                            if (myTicket?.status != "Attended") {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clickable { if (!isQrRevealed) isQrRevealed = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        bitmap = qrBitmap.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .blur(if (isQrRevealed) 0.dp else 16.dp)
                                    )
                                    if (!isQrRevealed) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(androidx.compose.material3.MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Tap to reveal",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .background(androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "${qrTimeRemaining}s",
                                                fontWeight = FontWeight.Bold,
                                                color = androidx.compose.material3.MaterialTheme.colorScheme.background
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "Attended",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary // Green
                                )
                            }
                            
                            if (myTicket != null) {
                                Spacer(modifier = Modifier.height(24.dp))
                                val statusColor = when (myTicket.status) {
                                    "Checked In" -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                                    "Attended" -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                                    else -> androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(16.dp).background(statusColor))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "STATUS: ${myTicket.status}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }
                                
                                if (myTicket.status == "Checked In" && myTicket.checkInTime != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    var timeStr: String? = null
                                    try {
                                        val checkInInstant = java.time.Instant.parse(myTicket.checkInTime)
                                        val localTime = java.time.LocalDateTime.ofInstant(checkInInstant, java.time.ZoneId.systemDefault())
                                        timeStr = java.time.format.DateTimeFormatter.ofPattern("hh:mm a").format(localTime)
                                    } catch (e: Exception) {}
                                    
                                    if (timeStr != null) {
                                        Text(
                                            text = "CHECKED IN AT: $timeStr",
                                            fontWeight = FontWeight.Bold,
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha=0.7f)
                                        )
                                    }
                                } else if (myTicket.status == "Attended" && myTicket.checkInTime != null && myTicket.checkOutTime != null) {
                                    var durationStr: String? = null
                                    try {
                                        val checkIn = java.time.Instant.parse(myTicket.checkInTime)
                                        val checkOut = java.time.Instant.parse(myTicket.checkOutTime)
                                        val durationMinutes = java.time.Duration.between(checkIn, checkOut).toMinutes()
                                        val hours = durationMinutes / 60
                                        val mins = durationMinutes % 60
                                        durationStr = "${hours}h ${mins}m"
                                    } catch (e: Exception) {}
                                    
                                    if (durationStr != null) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "TOTAL DURATION: $durationStr",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Black,
                                            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary // Hot Pink
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom section: Event Details
                    Text(
                        text = validEvent.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "BY ${validEvent.orgName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha=0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    DetailItem(label = "Description", value = validEvent.description)
                    DetailItem(label = "Date & Time", value = validEvent.date)
                    DetailItem(label = "Location", value = validEvent.location)
                    DetailItem(label = "Cause", value = validEvent.cause)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, thickness = 2.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItem(label = "Type of Work", value = validEvent.typeOfWork.ifBlank { "Not specified" })
                    DetailItem(label = "Payment / Perks", value = validEvent.payment.ifBlank { "Not specified" })
                    DetailItem(label = "Dress Code", value = validEvent.dressCode.ifBlank { "Not specified" })
                    DetailItem(label = "Contact Details", value = validEvent.contactDetails.ifBlank { "Not specified" })
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
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
        }
    }
}


}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary // Hot Pink
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
        )
    }
}

