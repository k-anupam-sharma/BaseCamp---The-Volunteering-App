package com.basecamp.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.basecamp.app.presentation.theme.brutalistStyle

@Composable
fun BrutalistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFAFF00), // Electric Yellow
    textColor: Color = Color.Black,
    cornerRadius: Dp = 0.dp // Sharp edges by default, can be adjusted
) {
    Box(
        modifier = modifier
            .brutalistStyle(cornerRadius = cornerRadius)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontWeight = FontWeight.ExtraBold, // Geometric and bold
            letterSpacing = 1.dp
        )
    }
}

@Composable
fun BrutalistCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    cornerRadius: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .brutalistStyle(cornerRadius = cornerRadius)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .padding(contentPadding)
    ) {
        content()
    }
}
