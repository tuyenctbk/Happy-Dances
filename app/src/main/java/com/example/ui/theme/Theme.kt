package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

private val DarkColorScheme = darkColorScheme(
    primary = CheerfulPrimaryLight,
    onPrimary = Color(0xFF5C0024),
    primaryContainer = Color(0xFF8C1D40),
    onPrimaryContainer = Color(0xFFFFD9E2),
    secondary = CheerfulSecondaryLight,
    onSecondary = Color(0xFF00354E),
    secondaryContainer = Color(0xFF075985),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = CheerfulTertiaryYellow,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = DarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = CheerfulPrimaryPink,
    onPrimary = Color.White,
    primaryContainer = CheerfulPrimaryContainer,
    onPrimaryContainer = CheerfulOnPrimaryContainer,
    secondary = CheerfulSecondaryBlue,
    onSecondary = Color.White,
    secondaryContainer = CheerfulSecondaryContainer,
    onSecondaryContainer = CheerfulOnSecondaryContainer,
    tertiary = CheerfulTertiaryYellow,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = CheerfulTertiaryContainer,
    onTertiaryContainer = CheerfulOnTertiaryContainer,
    background = CreamBackground,
    onBackground = TextCharcoal,
    surface = SoftSurface,
    onSurface = TextCharcoal,
    surfaceVariant = SoftSurfaceVariant,
    onSurfaceVariant = TextGray
)

fun BorderStrokeColors(width: Dp, color: Color): BorderStroke {
    return BorderStroke(width, color)
}

@Composable
fun HappyDancesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
