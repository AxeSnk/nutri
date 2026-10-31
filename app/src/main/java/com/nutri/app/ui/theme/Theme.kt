package com.nutri.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Палитра по design.jpg: тёмный графит, коралловый акцент, мятная вторичная
private val Coral = Color(0xFFF4593F)      // «Chef recommendation», кнопки
private val CoralLight = Color(0xFFFF7A5C)
private val Mint = Color(0xFF63B27F)       // успешные калории, прогресс
private val Ink = Color(0xFF121417)        // фон тёмной темы
private val InkSurface = Color(0xFF1B1E22) // карточки
private val InkVariant = Color(0xFF262A30) // поля ввода, чипы

private val LightColors = lightColorScheme(
    primary = Coral,
    onPrimary = Color.White,
    secondary = Mint,
    onSecondary = Color(0xFF0E2A1B),
    tertiary = Color(0xFFE8A33D),
    background = Color(0xFFF7F5F2),
    onBackground = Color(0xFF1B1B1F),
    surface = Color.White,
    surfaceVariant = Color(0xFFEEEAE6),
    outline = Color(0xFFB7AFa9)
)

private val DarkColors = darkColorScheme(
    primary = Coral,
    onPrimary = Color.White,
    secondary = Mint,
    onSecondary = Color(0xFF0E2A1B),
    tertiary = Color(0xFFE8A33D),
    background = Ink,
    onBackground = Color(0xFFECEAE8),
    surface = InkSurface,
    onSurface = Color(0xFFECEAE8),
    surfaceVariant = InkVariant,
    onSurfaceVariant = Color(0xFFB9B4B0),
    outline = Color(0xFF54524F)
)

/** Material 3 тема. По умолчанию — тёмная, как на макете design.jpg. */
@Composable
fun NutriTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
