package org.v.trace.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Premium iOS Dark Theme Palette
val Charcoal = Color(0xFF121212)      // Deep background
val SurfaceGray = Color(0xFF1C1C1E)   // iOS Surface
val IOSBlue = Color(0xFF0A84FF)       // Vibrant iOS Blue
val IOSBlueDark = Color(0xFF0056B3)
val TextGray = Color(0xFF8E8E93)
val GlassWhite = Color(0x0DFFFFFF)    // Subtle Cam effect
val GlassBorder = Color(0x1AFFFFFF)   // Subtle Cam border

val DarkColorScheme = darkColorScheme(
    primary = IOSBlue,
    onPrimary = Color.White,
    background = Charcoal,
    onBackground = Color.White,
    surface = SurfaceGray,
    onSurface = Color.White,
    secondary = TextGray
)
