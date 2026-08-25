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
    
    // New Advanced Fields
    var typeOfWork by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }
    var dressCode by remember { mutableStateOf("") }
    var contactDetails by remember { mutableStateOf("") }
    
    val causes = listOf("Environment", "Education", "Health", "Community", "Other")
    var expanded by remember { mutableStateOf(false) }

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
            text = "Create Event",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Title", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = title, onValueChange = { title = it }, placeholder = "e.g. Beach Cleanup")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Description", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = description, onValueChange = { description = it }, placeholder = "Details about the event")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Date", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = date, onValueChange = { date = it }, placeholder = "MM/DD/YYYY")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Location", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = location, onValueChange = { location = it }, placeholder = "e.g. Marine Drive")

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Max Volunteers", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = maxVolunteers, onValueChange = { maxVolunteers = it }, placeholder = "e.g. 50")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Type of Work", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = typeOfWork, onValueChange = { typeOfWork = it }, placeholder = "e.g. Physical labor, Teaching")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "PAYMENT / PERKS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = payment, onValueChange = { payment = it }, placeholder = "e.g. Unpaid, Free Lunch")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Dress Code", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = dressCode, onValueChange = { dressCode = it }, placeholder = "e.g. Casual, Closed-toe shoes")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Contact Details", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        GlacierTextField(value = contactDetails, onValueChange = { contactDetails = it }, placeholder = "e.g. +91 9876543210")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Cause", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
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
                text = "Submit Event",
                onClick = { 
                    val maxVols = maxVolunteers.toIntOrNull() ?: 0
                    viewModel.createEvent(
                        title, description, date, cause, location, "My Organization", maxVols,
                        typeOfWork, payment, dressCode, contactDetails
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

