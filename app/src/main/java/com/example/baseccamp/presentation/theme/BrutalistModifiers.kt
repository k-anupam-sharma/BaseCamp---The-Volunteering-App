package com.example.baseccamp.presentation.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom Compose Modifier that applies the Neo-Brutalist design language.
 * This includes a thick solid border and a solid (zero blur) drop shadow.
 */
fun Modifier.brutalistStyle(
    borderWidth: Dp = 2.dp,
    borderColor: Color = Color.Black,
    shadowOffset: Dp = 4.dp,
    shadowColor: Color = Color.Black,
    cornerRadius: Dp = 0.dp
): Modifier = this
    .drawBehind {
        // Draw the solid hard shadow behind the component
        drawRoundRect(
            color = shadowColor,
            topLeft = Offset(shadowOffset.toPx(), shadowOffset.toPx()),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
            style = Fill
        )
    }
    .border(
        width = borderWidth,
        color = borderColor,
        shape = RoundedCornerShape(cornerRadius)
    )

