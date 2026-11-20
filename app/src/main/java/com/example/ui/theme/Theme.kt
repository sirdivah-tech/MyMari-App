package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldBright,
    onPrimary = Color.White,
    primaryContainer = ForestGreenMedium,
    onPrimaryContainer = NeonLime,
    secondary = NeonLime,
    onSecondary = ForestGreenDark,
    secondaryContainer = CardBgDark,
    onSecondaryContainer = NeonLime,
    tertiary = AlertOrange,
    onTertiary = Color.White,
    background = CanvasBgDark,
    onBackground = TextPrimaryDark,
    surface = CardBgDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1D3628),
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldBrand,
    onPrimary = Color.White,
    primaryContainer = SoftMintContainer,
    onPrimaryContainer = ForestGreenDark,
    secondary = EmeraldBright,
    onSecondary = Color.White,
    secondaryContainer = NeonLimeSoft,
    onSecondaryContainer = ForestGreenDark,
    tertiary = AlertOrange,
    onTertiary = Color.White,
    background = CanvasBgLight,
    onBackground = TextPrimary,
    surface = CardBgLight,
    onSurface = TextPrimary,
    surfaceVariant = SoftMintSurface,
    onSurfaceVariant = TextSecondary,
    outline = SoftMintBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
