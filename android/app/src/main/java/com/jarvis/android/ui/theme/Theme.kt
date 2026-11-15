package com.jarvis.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Exclusive JARVIS palette — neon cyan + deep space
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D4FF),
    onPrimary = Color(0xFF003544),
    primaryContainer = Color(0xFF004D61),
    secondary = Color(0xFF7F52FF),
    tertiary = Color(0xFFFF6B35),
    background = Color(0xFF0A0E1A),
    surface = Color(0xFF141A2E),
    surfaceVariant = Color(0xFF1E2640),
    onBackground = Color(0xFFE6E8F0),
    onSurface = Color(0xFFE6E8F0),
    error = Color(0xFFFF5252),
    outline = Color(0xFF2A3358),
    scrim = Color(0x990A0E1A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    secondary = Color(0xFF7F52FF),
    tertiary = Color(0xFFFF6B35),
    background = Color(0xFFF8F9FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8EAFF),
    onBackground = Color(0xFF0A0E1A),
    onSurface = Color(0xFF0A0E1A),
)

// Extra JARVIS semantic colors
object JarvisColors {
    val NeonCyan = Color(0xFF00D4FF)
    val DeepSpace = Color(0xFF0A0E1A)
    val CardDark = Color(0xFF141A2E)
    val CardLight = Color(0xFF1E2640)
    val AccentPurple = Color(0xFF7F52FF)
    val Success = Color(0xFF00E676)
    val Warning = Color(0xFFFFAB00)
    val GradientPrimary = listOf(Color(0xFF00D4FF), Color(0xFF7F52FF))
    val GradientDark = listOf(Color(0xFF0A0E1A), Color(0xFF141A2E), Color(0xFF1A237E))
    val Glass = Color(0x14FFFFFF)
    val GlassBorder = Color(0x1A00D4FF)
}

@Composable
fun JarvisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for brand consistency, enable if user wants Material You
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = JarvisTypography, content = content)
}
