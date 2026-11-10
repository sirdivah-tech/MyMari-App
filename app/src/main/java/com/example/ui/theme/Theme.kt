package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonLime,
    onPrimary = ForestGreenDark,
    primaryContainer = ForestGreenMedium,
    onPrimaryContainer = NeonLime,
    secondary = ForestGreenLight,
    onSecondary = Color.White,
    secondaryContainer = CapsuleDarkGreen,
    onSecondaryContainer = NeonLime,
    tertiary = AlertOrange,
    onTertiary = Color.White,
    background = CanvasBgDark,
    onBackground = TextPrimaryDark,
    surface = CardBgDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CapsuleDarkGreen,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = NeonLime,
    onPrimaryContainer = ForestGreenDark,
    secondary = ForestGreenMedium,
    onSecondary = Color.White,
    secondaryContainer = NeonLimeSoft,
    onSecondaryContainer = ForestGreenDark,
    tertiary = AlertOrange,
    onTertiary = Color.White,
    background = CanvasBgLight,
    onBackground = TextPrimary,
    surface = CardBgLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFEBF2EC),
    onSurfaceVariant = TextSecondary,
    outline = CardBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
