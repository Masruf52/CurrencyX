package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = ProfessionalPrimary,
    onPrimary = Color.White,
    primaryContainer = ProfessionalPrimaryContainer,
    onPrimaryContainer = ProfessionalOnPrimaryContainer,
    secondary = GrowthGreen,
    onSecondary = Color.White,
    secondaryContainer = GrowthGreenContainer,
    onSecondaryContainer = OnGrowthGreenContainer,
    tertiary = TertiaryAccent,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = OnSurfaceLight,
    surface = LightSurface,
    onSurface = OnSurfaceLight,
    surfaceVariant = LightSurfaceContainer,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnSurfaceLight,
    primaryContainer = ProfessionalPrimary,
    onPrimaryContainer = Color.White,
    secondary = SecondaryDark,
    onSecondary = OnSurfaceLight,
    secondaryContainer = GrowthGreen,
    onSecondaryContainer = Color.White,
    tertiary = TertiaryContainer,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = OnSurfaceDark,
    surface = DarkSurface,
    onSurface = OnSurfaceDark,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OnSurfaceVariantDark,
    outlineVariant = DarkSurfaceContainerHigh
)

@Composable
fun CurrencyXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CurrencyXTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
