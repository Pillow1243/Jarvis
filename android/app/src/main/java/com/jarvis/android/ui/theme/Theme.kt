package com.jarvis.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    secondary = Color(0xFF7F52FF),
    tertiary = Color(0xFFFF6B35),
    background = Color(0xFFF8F9FF),
    surface = Color.White,
)

@Composable
fun JarvisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
