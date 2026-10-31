package com.nutri.app.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutri.app.core.domain.model.Macros
import com.nutri.app.core.domain.model.Recommendation
import com.nutri.app.ui.theme.NutriColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Общий стиль блока-панели по макету: скруглённый прямоугольник #252525
 * с тонкой обводкой. Заголовок и подзаголовок находятся ВНУТРИ панели,
 * поэтому блок выделяется вместе с заголовком.
 */
@Composable
fun DesignPanel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = NutriColors.Panel,
        border = androidx.compose.foundation.BorderStroke(1.dp, NutriColors.Field)
    ) {
        Column {
            SectionTitle(title, subtitle, Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp))
            content()
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel, onAvatarClick: () -> Unit = {}) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Header(state, onAvatarClick) }
        item {
            DesignPanel("Рекомендации шефа", "подобрано по вашей цели", Modifier.fillMaxWidth()) {
                RecommendationRow(state.recommendations, Modifier.padding(horizontal = 12.dp, vertical = 12.dp))
            }
        }
        item {
            DesignPanel("План питания", "сбалансированный план на неделю", Modifier.fillMaxWidth()) {
                WeekPlanRow(state.weekPlan, Modifier.padding(horizontal = 12.dp, vertical = 12.dp))
            }
        }
        item { DailyDietGoalCard(state.eatenKcal, state.eatenMacros, state.goal) }
    }
}

@Composable
private fun Header(state: HomeUiState, onAvatarClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Аватар с инициалом — клик открывает настройки профиля
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(NutriColors.Card)
                .clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center
        ) { Text(state.initial, color = NutriColors.White, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("С возвращением,", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(state.greetingName, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = NutriColors.White)
        }
        Spacer(Modifier.weight(1f))
        // Индикатор потребления сегодня: съедено / цель
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${state.eatenKcal.toInt()} / ${state.goal?.dailyKcal ?: 0} ккал",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("всё ещё", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(title, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = NutriColors.White)
            Spacer(Modifier.weight(1f))
        }
        Text(subtitle, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Лента карточек рекомендаций, как «Chef recommendation» на макете. */
@Composable
private fun RecommendationRow(recs: List<Recommendation>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(recs, key = { _, r -> r.dish.id }) { index, rec ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }
            val alpha by animateFloatAsState(if (visible) 1f else 0f,
                tween(300, delayMillis = index * 60), label = "appear")
            Card(
                modifier = Modifier
                    .width(190.dp)
                    .alpha(alpha),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NutriColors.RecipeCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NutriColors.Card)
            ) {
                Column(Modifier.padding(14.dp)) {
                    // «Фото» блюда — цветная подложка с эмодзи-заглушкой (картинки подтянутся из сети)
                    val tint = listOf(NutriColors.Meat, NutriColors.Pepper, NutriColors.Greens, NutriColors.Sauce)[index % 4]
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(tint),
                        contentAlignment = Alignment.Center
                    ) { Text("🍳", fontSize = 28.sp) }
                    Spacer(Modifier.height(10.dp))
                    Text(rec.dish.name, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold, maxLines = 1, color = NutriColors.Card)
                    Text(
                        "${rec.dish.kcal} ккал · Б ${rec.dish.protein.toInt()} Ж ${rec.dish.fat.toInt()} У ${rec.dish.carbs.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NutriColors.Meat
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { /* добавление в дневник — см. конструктор */ },
                        modifier = Modifier.fillMaxWidth().height(34.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) { Text("В дневник", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}

/** 7 дней плана с сегментами завтрак/обед/ужин — цветные полоски как на макете. */
@Composable
private fun WeekPlanRow(plan: List<PlanDay>, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(plan, key = { _, d -> d.date.toEpochDay() }) { index, day ->
            val isToday = day.date == today
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isToday) NutriColors.Selected else NutriColors.Card
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isToday) NutriColors.Selected else NutriColors.Field
                ),
                modifier = Modifier.width(92.dp)
            ) {
                Column(
                    Modifier.padding(vertical = 14.dp, horizontal = 8.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("ru")).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isToday) NutriColors.White else NutriColors.TextSecondary
                    )
                    Text(
                        day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = if (isToday) NutriColors.White else NutriColors.TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    // Сегменты приёмов пищи: высота ~ доля ккал, цвет по типу блюда
                    MealSegment(day.breakfastKcal, NutriColors.Meat, isToday)
                    MealSegment(day.lunchKcal, NutriColors.Pepper, isToday)
                    MealSegment(day.dinnerKcal, NutriColors.Greens, isToday)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${day.totalKcal.toInt()} ккал",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) NutriColors.White else NutriColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MealSegment(kcal: Float, color: androidx.compose.ui.graphics.Color, isToday: Boolean) {
    Box(
        Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth()
            .height((kcal / 25f).coerceIn(8f, 36f).dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isToday) color.copy(alpha = 0.85f) else color)
    )
}

/**
 * «Дневная цель»: красные столбцы с градиентом к #B00000, белые цифры,
 * #B0B0B0 подписи — как на макете. Фон блока #252525 (DesignPanel).
 */
@Composable
private fun DailyDietGoalCard(eatenKcal: Float, eaten: Macros, goal: com.nutri.app.core.domain.model.Goal?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NutriColors.Panel,
        border = androidx.compose.foundation.BorderStroke(1.dp, NutriColors.Field)
    ) {
        Row(
            Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            GoalBar(
                "ккал", eatenKcal, (goal?.dailyKcal ?: 0).toFloat(),
                NutriColors.Red, NutriColors.RedDark
            )
            GoalBar("жиры", eaten.fat, (goal?.dailyFat ?: 0).toFloat(), NutriColors.Orange)
            GoalBar("углев.", eaten.carbs, (goal?.dailyCarbs ?: 0).toFloat(), NutriColors.Greens)
            GoalBar("белки", eaten.protein, (goal?.dailyProtein ?: 0).toFloat(), NutriColors.Orange)
        }
    }
}

/**
 * Красный столбец: градиент от ярко-красного #FF3B30 до #B00000 по глубине.
 */
@Composable
private fun GoalBar(
    label: String,
    value: Float,
    target: Float,
    colorStart: androidx.compose.ui.graphics.Color,
    colorEnd: androidx.compose.ui.graphics.Color = colorStart
) {
    val fraction = if (target > 0) (value / target).coerceIn(0f, 1f) else 0f
    val progress by animateFloatAsState(fraction, tween(600), label = "bar")
    val gradient = androidx.compose.ui.graphics.Brush.verticalGradient(
        0f to colorStart, 1f to colorEnd
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .width(26.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(gradient)
        ) {
            // Заполнение снизу вверх
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height((120 * progress).dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(gradient)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("${value.toInt()}", style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold, color = NutriColors.White)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = NutriColors.TextSecondary)
    }
}
