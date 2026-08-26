package com.example.basecamp.presentation.screens.volunteer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.basecamp.utils.QrCodeGenerator
import org.json.JSONObject
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.basecamp.R
import com.example.basecamp.presentation.screens.shared.EventChatSection
import com.example.basecamp.presentation.screens.shared.EventChatViewModel

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
    val chatState by chatViewModel.chatState.collectAsState()
    
    val myTickets by viewModel.myTickets.collectAsState()
    val myTicket = myTickets.find { it.eventId == eventId }
    val isRsvped = myTicket != null

    var showQrDialog by remember { mutableStateOf(false) }

    LaunchedEffect(feedState, eventId) {
        if (feedState is FeedState.Success) {
            val allEvents = (feedState as FeedState.Success).availableEvents + (feedState as FeedState.Success).rsvpedEvents
            event = allEvents.find { it.id == eventId }
        }
    }

    LaunchedEffect(eventId) {
        chatViewModel.loadComments(eventId)
    }

    val darkBrown = Color(0xFF332B25)
    val lighterBrown = Color(0xFF4C3F35)

    Box(modifier = Modifier.fillMaxSize().background(darkBrown)) {
        if (event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            val validEvent = event!!
            
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // Top Banner Box
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    if (validEvent.bannerUrl != null) {
                        AsyncImage(
                            model = validEvent.bannerUrl,
                            contentDescription = "Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
                    }
                    
                    // Gradient overlay to blend into background
                    Box(modifier = Modifier.fillMaxSize().background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, darkBrown),
                            startY = 500f
                        )
                    ))

                    // Back button
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.4f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
                
                // Content Section
                Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-40).dp)) {
                    Text(
                        text = validEvent.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 34.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = validEvent.date.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha=0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (isRsvped) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFFD3A270), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RSVP Confirmed", color = Color(0xFFD3A270), fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { 
                                if (!isRsvped) {
                                    viewModel.rsvpForEvent(validEvent.id!!)
                                } else {
                                    showQrDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lighterBrown)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isRsvped) "Show Ticket" else "Attend", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { /* Contact functionality */ },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = lighterBrown)
                        ) {
                            Text("Contact", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Location
                    Text("Location", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(validEvent.location, fontSize = 15.sp, color = Color.White.copy(alpha=0.7f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (validEvent.locationLink.isNotBlank()) {
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        Text(
                            text = "View on Maps",
                            color = Color(0xFFD3A270), // Bronze color to match the theme
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    val url = validEvent.locationLink
                                    val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
                                    try {
                                        uriHandler.openUri(finalUrl)
                                    } catch (e: Exception) {
                                        // Silent catch if URL is completely invalid
                                    }
                                }
                                .padding(vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Hosts
                    Text("Hosts", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color.White, androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(validEvent.orgName.take(1).uppercase(), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(validEvent.orgName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // About
                    Text("About the event", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = validEvent.description.ifBlank { "No description provided for this event." }, 
                        fontSize = 15.sp, 
                        color = Color.White.copy(alpha=0.8f), 
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Additional Details
                    if (validEvent.typeOfWork.isNotBlank() || validEvent.payment.isNotBlank() || validEvent.dressCode.isNotBlank()) {
                        Text("Additional Details", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (validEvent.typeOfWork.isNotBlank()) DetailItem("Type of Work", validEvent.typeOfWork)
                        if (validEvent.payment.isNotBlank()) DetailItem("Perks / Payment", validEvent.payment)
                        if (validEvent.dressCode.isNotBlank()) DetailItem("Dress Code", validEvent.dressCode)
                        
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                    
                    // Chat Section (Integrated at bottom)
                    Text("Discussion", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
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
                    
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // QR Code Dialog
            if (showQrDialog && myTicket != null) {
                AlertDialog(
                    onDismissRequest = { showQrDialog = false },
                    containerColor = Color(0xFF1E1E1E),
                    title = {
                        Text("Your Ticket", color = Color.White, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            val payload = JSONObject().apply {
                                put("eventId", validEvent.id)
                                put("volunteerId", currentUserId)
                            }.toString()
                            
                            val qrBitmap = remember(payload) {
                                QrCodeGenerator.generateQrCode(payload)
                            }
                            
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier.size(200.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Status: ${myTicket.status}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showQrDialog = false }) {
                            Text("Close", color = Color(0xFFD3A270))
                        }
                    }
                )
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
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD3A270) // bronze tone
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha=0.9f)
        )
    }
}
