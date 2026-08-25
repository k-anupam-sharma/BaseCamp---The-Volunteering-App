package com.example.basecamp.presentation.screens.volunteer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basecamp.presentation.components.GlassPanel
import com.example.basecamp.presentation.components.AnimatedBackground

data class Badge(val id: String, val title: String, val icon: String)

@Composable
fun VolunteerProfileScreen() {
    // Dummy data for now. In a real app, this would come from a ViewModel
    val totalHours = 142
    val volunteerName = "Alex Johnson"
    val badges = listOf(
        Badge("1", "Early Bird", "🌅"),
        Badge("2", "Eco Warrior", "🌳"),
        Badge("3", "TOP 10%", "🏆"),
        Badge("4", "50+ HOURS", "⭐"),
        Badge("5", "Team Player", "🤝"),
        Badge("6", "First RSVP", "⚡")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
             // Off-White/Beige
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High-energy player card header
        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Volunteer Dossier",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha=0.7f),
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = volunteerName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Total Hours",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$totalHours",
                    fontSize = 80.sp, // Massive typography
                    fontWeight = FontWeight.Black,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary, // Electric Yellow
                    style = TextStyle()
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Earned Badges",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(badges) { badge ->
                GlassPanel(
                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant, // Hot Pink
                    modifier = Modifier.aspectRatio(1f) // Square badges
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = badge.icon,
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = badge.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
    }
}


