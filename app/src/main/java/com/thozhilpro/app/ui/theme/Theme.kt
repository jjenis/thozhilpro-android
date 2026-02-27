package com.thozhilpro.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Indigo = Color(0xFF6366F1)
val IndigoDark = Color(0xFF4F46E5)
val IndigoLight = Color(0xFF818CF8)
val Purple = Color(0xFF8B5CF6)
val Green = Color(0xFF10B981)
val Red = Color(0xFFEF4444)
val Orange = Color(0xFFF59E0B)
val Blue = Color(0xFF3B82F6)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = Color.White,
    primaryContainer = IndigoDark,
    secondary = Purple,
    tertiary = Green,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    error = Red,
    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    secondary = Purple,
    tertiary = Green,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B),
    error = Red,
    outline = Color(0xFFE2E8F0)
)

@Composable
fun ThozhilProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
