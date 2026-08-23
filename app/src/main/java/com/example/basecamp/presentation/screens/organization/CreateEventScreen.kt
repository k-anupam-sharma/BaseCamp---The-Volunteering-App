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
import com.example.basecamp.presentation.components.BrutalistButton
import com.example.basecamp.presentation.components.BrutalistTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F0))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "CREATE EVENT",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "TITLE", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = title, onValueChange = { title = it }, placeholder = "e.g. Beach Cleanup")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "DESCRIPTION", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = description, onValueChange = { description = it }, placeholder = "Details about the event")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "DATE", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = date, onValueChange = { date = it }, placeholder = "MM/DD/YYYY")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "LOCATION", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = location, onValueChange = { location = it }, placeholder = "e.g. Marine Drive")

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "MAX VOLUNTEERS", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = maxVolunteers, onValueChange = { maxVolunteers = it }, placeholder = "e.g. 50")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "TYPE OF WORK", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = typeOfWork, onValueChange = { typeOfWork = it }, placeholder = "e.g. Physical labor, Teaching")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "PAYMENT / PERKS", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = payment, onValueChange = { payment = it }, placeholder = "e.g. Unpaid, Free Lunch")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "DRESS CODE", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = dressCode, onValueChange = { dressCode = it }, placeholder = "e.g. Casual, Closed-toe shoes")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "CONTACT DETAILS", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        BrutalistTextField(value = contactDetails, onValueChange = { contactDetails = it }, placeholder = "e.g. +91 9876543210")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "CAUSE", fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(modifier = Modifier.menuAnchor()) {
                BrutalistTextField(
                    value = cause,
                    onValueChange = {},
                    placeholder = "Select a cause",
                    readOnly = true
                )
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                causes.forEach { selectedCause ->
                    DropdownMenuItem(
                        text = { Text(selectedCause, fontWeight = FontWeight.Bold, color = Color.Black) },
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
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            BrutalistButton(
                text = "SUBMIT EVENT",
                onClick = { 
                    val maxVols = maxVolunteers.toIntOrNull() ?: 0
                    viewModel.createEvent(
                        title, description, date, cause, location, "My Organization", maxVols,
                        typeOfWork, payment, dressCode, contactDetails
                    ) 
                },
                backgroundColor = Color(0xFFFAFF00), // Electric Yellow
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (createState is CreateEventState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (createState as CreateEventState.Error).message,
                color = Color(0xFFFF007F), // Hot Pink
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}


