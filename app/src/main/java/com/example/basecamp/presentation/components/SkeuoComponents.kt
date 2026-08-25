package com.example.basecamp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.skeuoCard(shape: Shape = RoundedCornerShape(40.dp)): Modifier = this
    .shadow(
        elevation = 20.dp,
        shape = shape,
        spotColor = Color(0xCC000000), // 80% opacity black
        ambientColor = Color(0x80000000)
    )
    .background(
        color = Color(0x662B2B2B), // surface-container at 60%
        shape = shape
    )
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.1f),
                Color.Transparent
            )
        ),
        shape = shape
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.15f), // top bevel
                Color.Black.copy(alpha = 0.5f)   // bottom inner shadow
            )
        ),
        shape = shape
    )

fun Modifier.skeuoButtonPrimary(shape: Shape = RoundedCornerShape(50)): Modifier = this
    .shadow(
        elevation = 6.dp,
        shape = shape,
        spotColor = Color(0x80000000), // 50% opacity
        ambientColor = Color(0x80000000)
    )
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF5C6BC0), Color(0xFF3F51B5))
        ),
        shape = shape
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.4f)
            )
        ),
        shape = shape
    )

fun Modifier.skeuoButtonSecondary(shape: Shape = RoundedCornerShape(50)): Modifier = this
    .shadow(
        elevation = 6.dp,
        shape = shape,
        spotColor = Color(0x80000000), // 50% opacity
        ambientColor = Color(0x80000000)
    )
    .background(
        color = Color(0x4D201F1F), // 30% surface-container
        shape = shape
    )
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)
        ),
        shape = shape
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.4f)
            )
        ),
        shape = shape
    )

fun Modifier.skeuoIcon(shape: Shape = RoundedCornerShape(50)): Modifier = this
    .background(
        color = Color(0x802B2B2B), // surface-container-lowest at 50%
        shape = shape
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.6f),  // top inner shadow
                Color.White.copy(alpha = 0.1f)   // bottom bevel
            )
        ),
        shape = shape
    )


