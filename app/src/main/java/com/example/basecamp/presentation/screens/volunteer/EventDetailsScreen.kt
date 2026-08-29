package com.example.basecamp.presentation.screens.volunteer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.screens.shared.EventChatSection
import com.example.basecamp.presentation.screens.shared.EventChatViewModel

@Composable
fun EventDetailsScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTicket: (String) -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel(),
    chatViewModel: EventChatViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    var event by remember { mutableStateOf<Event?>(null) }
    
    val myTickets by viewModel.myTickets.collectAsState()
    val myTicket = myTickets.find { it.eventId == eventId }
    val isRsvped = myTicket != null

    val context = LocalContext.current
    val rsvpState by viewModel.rsvpState.collectAsState()

    LaunchedEffect(rsvpState) {
        when (rsvpState) {
            is RsvpState.Success -> {
                Toast.makeText(context, "Ticket secured!", Toast.LENGTH_SHORT).show()
                viewModel.resetRsvpState()
            }
            is RsvpState.Error -> {
                Toast.makeText(context, (rsvpState as RsvpState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetRsvpState()
            }
            else -> {}
        }
    }

    LaunchedEffect(feedState, eventId) {
        if (feedState is FeedState.Success) {
            val allEvents = (feedState as FeedState.Success).availableEvents + (feedState as FeedState.Success).rsvpedEvents
            event = allEvents.find { it.id == eventId }
        }
    }

    LaunchedEffect(eventId) {
        chatViewModel.loadComments(eventId)
    }

    val bgColor = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E)
    val primaryAccent = Color(0xFFFF4B4B)
    val textPrimary = Color.White
    val textSecondary = Color(0xFFA0A0A0)

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        if (event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryAccent)
            }
        } else {
            val validEvent = event!!
            
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // Hero Image
                Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    if (validEvent.bannerUrl != null) {
                        AsyncImage(
                            model = validEvent.bannerUrl,
                            contentDescription = "Event Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                    }
                    
                    // Gradient overlay to blend into background
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, bgColor),
                                startY = 600f
                            )
                        )
                    )

                    // Top Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { onNavigateBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { /* Share */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }

                    // Price Pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(24.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Free", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                // Details Content
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = validEvent.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cause: ${validEvent.cause} • ${validEvent.location}",
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Date & Time", fontSize = 12.sp, color = textSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(validEvent.date, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(cardColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("About this event", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = validEvent.description,
                        fontSize = 14.sp,
                        color = textSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(Icons.Default.LocationOn, validEvent.location)
                    DetailRow(Icons.Default.Person, "Organizer: ${validEvent.orgName}")
                    DetailRow(Icons.Default.Build, validEvent.typeOfWork)
                    if (validEvent.payment.isNotBlank()) DetailRow(Icons.Default.AttachMoney, validEvent.payment)

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Chat Section
                    val currentChatState by chatViewModel.chatState.collectAsState()
                    EventChatSection(
                        chatState = currentChatState,
                        eventOwnerId = validEvent.orgId ?: "",
                        onAddComment = { text, parentId ->
                            chatViewModel.addComment(eventId, text, parentId)
                        },
                        onToggleLike = { commentId ->
                            chatViewModel.toggleLike(commentId)
                        },
                        onRefresh = { chatViewModel.loadComments(eventId) }
                    )
                    
                    Spacer(modifier = Modifier.height(100.dp)) // Space for sticky bottom bar
                }
            }

            // Sticky Bottom Action Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, bgColor, bgColor),
                            startY = 0f
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(cardColor)
                            .clickable { /* Like */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = textSecondary)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val safeId = validEvent.id ?: ""
                            if (isRsvped) {
                                onNavigateToTicket(safeId)
                            } else {
                                viewModel.rsvpForEvent(safeId)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        if (rsvpState is RsvpState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isRsvped) "View Ticket" else "Get a Ticket",
                                color = Color.White,
                                fontSize = 16.sp,
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
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    if (text.isNotBlank()) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFA0A0A0), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color(0xFFA0A0A0), fontSize = 14.sp)
        }
    }
}
