package com.example.basecamp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BaseCampLogo(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFB085), // Peach/Orange from mockup
    sizeDp: Int = 48
) {
    Canvas(modifier = modifier.size(sizeDp.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        val strokeWidth = 2.dp.toPx()

        // Draw Hexagon
        val hexPath = Path()
        for (i in 0 until 6) {
            val angle = Math.PI / 3 * i - Math.PI / 2
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            if (i == 0) {
                hexPath.moveTo(x, y)
            } else {
                hexPath.lineTo(x, y)
            }
        }
        hexPath.close()

        drawPath(
            path = hexPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw stylized mountain inside (BaseCamp)
        val mountainPath = Path()
        // Peak
        mountainPath.moveTo(center.x, center.y - radius * 0.4f)
        // Left base
        mountainPath.lineTo(center.x - radius * 0.5f, center.y + radius * 0.5f)
        // Right base
        mountainPath.lineTo(center.x + radius * 0.5f, center.y + radius * 0.5f)
        mountainPath.close()

        drawPath(
            path = mountainPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        
        // Inner lines (crevasses)
        drawLine(
            color = color,
            start = Offset(center.x, center.y - radius * 0.4f),
            end = Offset(center.x - radius * 0.2f, center.y + radius * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
