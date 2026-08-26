package com.example.basecamp.presentation.screens.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basecamp.presentation.components.GlacierButton
import com.example.basecamp.presentation.components.AnimatedBackground
import com.example.basecamp.presentation.components.GlacierTextField
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    eventId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: OrgViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var cause by remember { mutableStateOf("") }
    var maxVolunteers by remember { mutableStateOf("") }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        bannerUri = uri
    }
    val context = LocalContext.current
    
    // New Advanced Fields
    var locationLink by remember { mutableStateOf("") }
    var isMultiDay by remember { mutableStateOf(false) }
    var endDate by remember { mutableStateOf("") }
    
    // New Advanced Fields
    var typeOfWork by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }
    var dressCode by remember { mutableStateOf("") }
    var contactDetails by remember { mutableStateOf("") }
    
    val causes = listOf("Environment", "Education", "Health", "Community", "Other")
    var expanded by remember { mutableStateOf(false) }
    
    var existingBannerUrl by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(eventId) {
        if (eventId != null) {
            viewModel.getEventById(eventId)?.let { event ->
                title = event.title
                description = event.description
                date = event.date
                location = event.location
                cause = event.cause
                maxVolunteers = event.maxVolunteers.toString()
                locationLink = event.locationLink
                isMultiDay = event.isMultiDay
                endDate = event.endDate
                typeOfWork = event.typeOfWork
                payment = event.payment
                dressCode = event.dressCode
                contactDetails = event.contactDetails
                existingBannerUrl = event.bannerUrl
            }
        }
    }

    val createState by viewModel.createState.collectAsState()

    LaunchedEffect(createState) {
        if (createState is CreateEventState.Success) {
            onNavigateBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()
        Column(
        modifier = Modifier
            .fillMaxSize()
            
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = if (eventId != null) "Update Event" else "Create Event",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Event Banner", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "Recommended aspect ratio 16:9", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.DarkGray)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (bannerUri == null && existingBannerUrl != null) {
                AsyncImage(
                    model = existingBannerUrl,
                    contentDescription = "Event Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (bannerUri != null) {
                AsyncImage(
                    model = bannerUri,
                    contentDescription = "Event Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Image, contentDescription = "Add Banner", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to add event banner", color = Color.LightGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Title *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = title, onValueChange = { title = it }, placeholder = "e.g. Beach Cleanup")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Description *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = description, onValueChange = { description = it }, placeholder = "Details about the event")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Multi-Day Event", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Switch(
                checked = isMultiDay,
                onCheckedChange = { isMultiDay = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF7DD3FC))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = if (isMultiDay) "Start Date *" else "Date *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(
            value = date,
            onValueChange = { date = it },
            placeholder = "DD-MM-YYYY",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        if (isMultiDay) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "End Date *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            GlacierTextField(
                value = endDate,
                onValueChange = { endDate = it },
                placeholder = "DD-MM-YYYY",
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Location *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = location, onValueChange = { location = it }, placeholder = "e.g. Marine Drive")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Location Link *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(
            value = locationLink,
            onValueChange = { locationLink = it },
            placeholder = "https://maps.google.com/...",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Uri)
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Max Volunteers *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(
            value = maxVolunteers,
            onValueChange = { maxVolunteers = it },
            placeholder = "e.g. 50",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Type of Work *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = typeOfWork, onValueChange = { typeOfWork = it }, placeholder = "e.g. Physical labor, Teaching")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "PAYMENT / PERKS *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = payment, onValueChange = { payment = it }, placeholder = "e.g. Unpaid, Free Lunch")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Dress Code *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = dressCode, onValueChange = { dressCode = it }, placeholder = "e.g. Casual, Closed-toe shoes")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contact Details *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(
            value = contactDetails,
            onValueChange = { contactDetails = it },
            placeholder = "e.g. +91 9876543210",
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Cause *", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(modifier = Modifier.menuAnchor()) {
                GlacierTextField(
                    value = cause,
                    onValueChange = {},
                    placeholder = "Select a cause",
                    readOnly = true
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                causes.forEach { selectedCause ->
                    DropdownMenuItem(
                        text = { Text(selectedCause, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                        onClick = {
                            cause = selectedCause
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (createState is CreateEventState.Loading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            GlacierButton(
                text = if (eventId != null) "Update Event" else "Submit Event",
                onClick = { 
                    viewModel.createEvent(
                        eventId = eventId,
                        title = title, description = description, date = date, cause = cause, location = location, orgName = "My Organization", maxVolunteersStr = maxVolunteers,
                        typeOfWork = typeOfWork, payment = payment, dressCode = dressCode, contactDetails = contactDetails, locationLink = locationLink, isMultiDay = isMultiDay, endDate = endDate,
                        context = context, bannerUri = bannerUri
                    ) 
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (createState is CreateEventState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (createState as CreateEventState.Error).message,
                color = Color(0xFF7DD3FC), // Hot Pink
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
    }
}

