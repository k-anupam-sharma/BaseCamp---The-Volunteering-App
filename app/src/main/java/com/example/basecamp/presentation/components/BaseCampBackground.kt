package com.example.basecamp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BaseCampBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5A0000), // Dark Red at the top
                        Color(0xFF230000), // Very Dark Red in middle
                        Color(0xFF0F0F0F)  // Black/Dark Grey at bottom
                    )
                )
            )
    ) {
        content()
    }
}
