package org.v.trace.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val Charcoal = Color(0xFF121212)
val SurfaceGray = Color(0xFF1E1E1E)
val IOSBlue = Color(0xFF007AFF)
val IOSBlueDark = Color(0xFF0056B3)
val TextGray = Color(0xFF8E8E93)
val GlassWhite = Color(0x1AFFFFFF)
val GlassBorder = Color(0x33FFFFFF)

val DarkColorScheme = darkColorScheme(
    primary = IOSBlue,
    onPrimary = Color.White,
    background = Charcoal,
    onBackground = Color.White,
    surface = SurfaceGray,
    onSurface = Color.White,
    secondary = TextGray
)
