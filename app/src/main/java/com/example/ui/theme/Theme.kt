package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MyMariExtendedColors(
    val isDark: Boolean,
    val canvasBg: Color,
    val cardBg: Color,
    val softSurface: Color,
    val softContainer: Color,
    val softBorder: Color,
    val accentPrimary: Color,
    val accentText: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val navDockBg: Color,
    val navDockBorder: Color,
    val navDockShadow: Color,
    val heroGradientTop: Color,
    val heroGradientBottom: Color,
    val pillBg: Color,
    val incomeGreen: Color,
    val expenseRed: Color,
    val alertOrange: Color,
    val warningBg: Color,
    val warningText: Color,
    val errorBg: Color,
    val errorText: Color,
    val limeCardBg: Color
)

private val LightExtendedColors = MyMariExtendedColors(
    isDark = false,
    canvasBg = CanvasBgLight,
    cardBg = CardBgLight,
    softSurface = SoftMintSurface,
    softContainer = SoftMintContainer,
    softBorder = SoftMintBorder,
    accentPrimary = EmeraldBrand,
    accentText = ForestGreenDark,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    navDockBg = NavDockBgLight,
    navDockBorder = NavDockBorderLight,
    navDockShadow = Color(0x3314532D),
    heroGradientTop = SoftMintSurface,
    heroGradientBottom = SoftMintContainer,
    pillBg = Color.White,
    incomeGreen = IncomeGreen,
    expenseRed = ExpenseRed,
    alertOrange = AlertOrange,
    warningBg = Color(0xFFFFF7ED),
    warningText = Color(0xFF9A3412),
    errorBg = Color(0xFFFEF2F2),
    errorText = Color(0xFFB91C1C),
    limeCardBg = NeonLimeSoft
)

private val DarkExtendedColors = MyMariExtendedColors(
    isDark = true,
    canvasBg = CanvasBgDark,
    cardBg = CardBgDark,
    softSurface = SoftMintSurfaceDark,
    softContainer = SoftMintContainerDark,
    softBorder = CardBorderDark,
    accentPrimary = EmeraldAccentDark,
    accentText = ForestTextDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    navDockBg = NavDockBgDark,
    navDockBorder = NavDockBorderDark,
    navDockShadow = Color(0x66000000),
    heroGradientTop = Color(0xFF152A1F),
    heroGradientBottom = Color(0xFF1E3B2B),
    pillBg = Color(0xFF122119),
    incomeGreen = IncomeGreenDark,
    expenseRed = ExpenseRedDark,
    alertOrange = AlertOrangeDark,
    warningBg = WarningBgDark,
    warningText = WarningTextDark,
    errorBg = ErrorBgDark,
    errorText = ErrorTextDark,
    limeCardBg = Color(0xFF233F2A)
)

val LocalMyMariColors = staticCompositionLocalOf { LightExtendedColors }

object MyMariTheme {
    val colors: MyMariExtendedColors
        @Composable
        get() = LocalMyMariColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldBright,
    onPrimary = Color.White,
    primaryContainer = SoftMintContainerDark,
    onPrimaryContainer = ForestTextDark,
    secondary = NeonLime,
    onSecondary = ForestGreenDark,
    secondaryContainer = SoftMintSurfaceDark,
    onSecondaryContainer = ForestTextDark,
    tertiary = AlertOrangeDark,
    onTertiary = Color.White,
    background = CanvasBgDark,
    onBackground = TextPrimaryDark,
    surface = CardBgDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SoftMintSurfaceDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark,
    error = ExpenseRedDark
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
    outline = SoftMintBorder,
    error = ExpenseRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalMyMariColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
