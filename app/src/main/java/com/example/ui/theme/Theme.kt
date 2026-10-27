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

private val DarkColorScheme = darkColorScheme(
    primary = VentoyCyanBright,
    onPrimary = Color(0xFF00363F),
    primaryContainer = Color(0xFF004F5C),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = VentoyIndigoBright,
    onSecondary = Color(0xFF1E1A60),
    secondaryContainer = Color(0xFF37337B),
    onSecondaryContainer = Color(0xFFE0E0FF),
    tertiary = VentoyEmeraldBright,
    onTertiary = Color(0xFF003920),
    tertiaryContainer = Color(0xFF005230),
    onTertiaryContainer = Color(0xFFA6F5C6),
    background = VentoyDarkBg,
    onBackground = VentoyTextPrimary,
    surface = VentoyDarkSurface,
    onSurface = VentoyTextPrimary,
    surfaceVariant = VentoyDarkSurfaceVariant,
    onSurfaceVariant = VentoyTextSecondary,
    outline = VentoyBorder,
    error = VentoyCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = VentoyCyanDim,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCBE6EC),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = VentoyIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0FF),
    onSecondaryContainer = Color(0xFF12104B),
    tertiary = VentoyEmerald,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = VentoyCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek cyberpunk dark for Ventoy+
    dynamicColor: Boolean = false, // Keep branded high-contrast cyberpunk scheme by default
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
