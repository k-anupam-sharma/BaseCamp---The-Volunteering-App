package com.example.basecamp.presentation.theme

import androidx.compose.ui.graphics.Color

// Salt and Pepper Palette
val SaltWhite = Color(0xFFFFFFFF)
val PepperLightGray = Color(0xFFD4D4D4)
val PepperGray = Color(0xFFB3B3B3)
val PepperDarkGray = Color(0xFF2B2B2B)

// Map to Skeuo names
val SkeuoPrimary = SaltWhite
val SkeuoOnPrimary = PepperDarkGray
val SkeuoBackground = Color.Black // Since we have a space background, base is black
val SkeuoSurface = PepperDarkGray
val SkeuoSurfaceVariant = Color(0x802B2B2B) // Translucent for glassmorphism
val SkeuoOnSurfaceVariant = SaltWhite
val SkeuoOnBackground = SaltWhite
val SkeuoTertiary = PepperLightGray
val SkeuoError = Color(0xFFE53935)

// Legacy compatibility
val GlacierPrimary = SkeuoPrimary
val GlacierBackground = SkeuoBackground
val GlacierSurface = SkeuoSurface
val GlacierSurfaceVariant = SkeuoSurfaceVariant
val GlacierOnBackground = SkeuoOnBackground
val GlacierTertiary = SkeuoTertiary
val GlacierError = SkeuoError

// Glass Legacy Compatibility
val GlassPanelBackground = Color(0x662B2B2B) // 40% opaque dark gray for that deep glass look
val GlassElevatedBackground = Color(0x802B2B2B) // 50% opaque dark gray
val GlassBorder = Color(0x40FFFFFF) // 25% white border
