package com.example.basecamp.presentation.screens.volunteer

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.BaseCampBackground
import com.example.basecamp.utils.QrCodeGenerator
import org.json.JSONObject

@Composable
fun TicketScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedState by viewModel.feedState.collectAsState()
    var event by remember { mutableStateOf<Event?>(null) }
    
    val myTickets by viewModel.myTickets.collectAsState()
    val ticket = myTickets.find { it.eventId == eventId }

    LaunchedEffect(feedState, eventId) {
        if (feedState is FeedState.Success) {
            val allEvents = (feedState as FeedState.Success).availableEvents + (feedState as FeedState.Success).rsvpedEvents
            event = allEvents.find { it.id == eventId }
        }
    }

    BaseCampBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { /* Share Ticket */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (event == null || ticket == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                val validEvent = event!!
                
                // The White Ticket Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color.White)
                            .drawBehind {
                                // Draw dashed line separator
                                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.5f),
                                    start = Offset(0f, size.height * 0.65f),
                                    end = Offset(size.width, size.height * 0.65f),
                                    strokeWidth = 4f,
                                    pathEffect = dashPathEffect
                                )
                            }
                    ) {
                        // Top Half: QR Code
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Scan This QR",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Point This QR To The Scan Place",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(32.dp))

                            val qrContent = JSONObject().apply {
                                put("ticketId", ticket.id)
                                put("eventId", validEvent.id ?: "")
                                put("userId", viewModel.currentUserId)
                            }.toString()

                            val qrBitmap = remember(qrContent) {
                                QrCodeGenerator.generateQrCode(qrContent, 600, 600)
                            }

                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier
                                        .size(220.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Bottom Half: Details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 24.dp)
                        ) {
                            Text(
                                text = validEvent.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Name", fontSize = 12.sp, color = Color.Gray)
                                    Text(viewModel.currentUserId ?: "Volunteer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                    Text("Date", fontSize = 12.sp, color = Color.Gray)
                                    // Parse date to just show date part if it has time
                                    val datePart = validEvent.date.split(" ").take(3).joinToString(" ")
                                    Text(datePart, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Location", fontSize = 12.sp, color = Color.Gray)
                                    Text(validEvent.location, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                    Text("Time", fontSize = 12.sp, color = Color.Gray)
                                    val timePart = validEvent.date.split(" ").drop(3).joinToString(" ")
                                    Text(timePart.ifBlank { "TBA" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }

                    // Left and Right Cutouts
                    val cutoutRadius = 24.dp
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = -cutoutRadius, y = (40).dp) // adjust y offset based on 65% proportion
                            .size(cutoutRadius * 2)
                            .clip(CircleShape)
                            .background(Color(0xFF230000)) // Matches the gradient middle roughly
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = cutoutRadius, y = (40).dp)
                            .size(cutoutRadius * 2)
                            .clip(CircleShape)
                            .background(Color(0xFF230000))
                    )
                }
            }
        }
    }
}
