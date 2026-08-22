package com.example.basecamp.presentation.screens.volunteer

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.basecamp.domain.model.Event
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistCard
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    event: Event,
    volunteerId: String,
    onNavigateBack: () -> Unit
) {
    var isRsvped by remember { mutableStateOf(false) }
    var showTicketDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0))
            .padding(24.dp)
    ) {
        BrutalistButton(
            text = "BACK",
            onClick = onNavigateBack,
            backgroundColor = Color.White
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        BrutalistCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = event.title.uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "ORG: ${event.orgName}", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(text = "CAUSE: ${event.cause}", fontWeight = FontWeight.Bold, color = Color(0xFFFF007F))
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(text = "📍 ${event.location}", fontWeight = FontWeight.Medium)
                Text(text = "🗓 ${event.date}", fontWeight = FontWeight.Medium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = event.description, fontWeight = FontWeight.Medium)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (!isRsvped) {
                    BrutalistButton(
                        text = "RSVP NOW",
                        onClick = { isRsvped = true },
                        backgroundColor = Color(0xFFFAFF00), // Electric Yellow
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    BrutalistButton(
                        text = "SHOW TICKET",
                        onClick = { showTicketDialog = true },
                        backgroundColor = Color(0xFF00E5FF), // Cyan for ticket
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showTicketDialog) {
        Dialog(onDismissRequest = { showTicketDialog = false }) {
            BrutalistCard(
                backgroundColor = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOUR TICKET",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scan at the event",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Generate QR Code containing eventId and volunteerId
                    val qrData = JSONObject().apply {
                        put("eventId", event.id)
                        put("volunteerId", volunteerId)
                    }.toString()
                    
                    val qrBitmap = generateQrCode(qrData, 512)
                    
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code Ticket",
                            modifier = Modifier.size(200.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("QR Code Error")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    BrutalistButton(
                        text = "CLOSE",
                        onClick = { showTicketDialog = false },
                        backgroundColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// Helper function using ZXing Core to generate QR Code Bitmap
fun generateQrCode(content: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


