package com.example.basecamp.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SkeuoPrimary,
    onPrimary = SkeuoOnPrimary,
    background = SkeuoBackground,
    surface = SkeuoSurface,
    surfaceVariant = SkeuoSurfaceVariant,
    onSurfaceVariant = SkeuoOnSurfaceVariant,
    onBackground = SkeuoOnBackground,
    onSurface = SkeuoOnBackground,
    tertiary = SkeuoTertiary,
    error = SkeuoError
)

@Composable
fun BaseCampTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
