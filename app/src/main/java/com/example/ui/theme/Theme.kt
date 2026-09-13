package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NeonGlassColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    secondary = NeonViolet,
    onSecondary = Color.White,
    tertiary = NeonPink,
    background = BgDark,
    onBackground = TextWhite,
    surface = BgDarkNavy,
    onSurface = TextWhite,
    surfaceVariant = CardGlassBg,
    onSurfaceVariant = TextMuted,
    outline = CardGlassBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NeonGlassColorScheme,
        typography = Typography,
        content = content
    )
}

