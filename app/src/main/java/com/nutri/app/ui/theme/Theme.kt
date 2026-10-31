package com.nutri.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Палитра по design.jpg: тёмные слои, красный акцент, приглушённо-оранжевый второй акцент. */
object NutriColors {
    // Фон и слои
    val Background = Color(0xFF121212)      // фон экрана
    val Container = Color(0xFF1E1E1E)       // крупные контейнеры/подложки
    val Panel = Color(0xFF252525)           // блоки (Meal Plan, Daily Diet Goal, верхняя панель)
    val Card = Color(0xFF2C2C2C)            // карточки, навигация, аватар
    val Field = Color(0xFF333333)           // поле поиска, обводки, «пустые» шкалы
    val Selected = Color(0xFF3A3A3A)        // выбранный день

    // Акценты
    val Red = Color(0xFFFF3B30)
    val RedDark = Color(0xFFB00000)         // низ градиента столбцов
    val Orange = Color(0xFFFF9F43)

    // Текст и иконки
    val White = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFFD3D3D3)
    val TextSecondary = Color(0xFFB0B0B0)
    val Icon = Color(0xFFAAAAAA)
    val Inactive = Color(0xFF888888)

    // Карточки рецептов (Chef recommendation)
    val RecipeCard = Color(0xFFFAF9F6)
    val Meat = Color(0xFF6B4A42)
    val Pepper = Color(0xFFFFB84D)
    val Greens = Color(0xFF4A9A5A)
    val Sauce = Color(0xFFF5F2EB)
}

private val LightColors = lightColorScheme(
    primary = NutriColors.Red,
    onPrimary = Color.White,
    secondary = NutriColors.Greens,
    onSecondary = Color.White,
    tertiary = NutriColors.Orange,
    background = Color(0xFFF7F5F2),
    onBackground = Color(0xFF1B1B1F),
    surface = Color.White,
    surfaceVariant = Color(0xFFEEEAE6),
    outline = Color(0xFFB7AFA9),
    outlineVariant = Color(0xFFDCD7D2)
)

private val DarkColors = darkColorScheme(
    primary = NutriColors.Red,
    onPrimary = NutriColors.White,
    secondary = NutriColors.Greens,
    onSecondary = NutriColors.White,
    tertiary = NutriColors.Orange,
    onTertiary = NutriColors.Background,
    background = NutriColors.Background,
    onBackground = NutriColors.White,
    surface = NutriColors.Panel,
    onSurface = NutriColors.TextPrimary,
    surfaceVariant = NutriColors.Card,
    onSurfaceVariant = NutriColors.TextSecondary,
    surfaceContainer = NutriColors.Card,          // фон NavigationBar/Rail
    outline = NutriColors.Inactive,
    outlineVariant = NutriColors.Field
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
