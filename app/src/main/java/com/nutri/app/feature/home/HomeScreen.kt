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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutri.app.core.domain.model.Macros
import com.nutri.app.core.domain.model.Recommendation
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Главный экран по design.jpg:
 *  «Chef recommendation» — горизонтальная лента карточек блюд,
 *  Meal Plan — 7 дней с цветными сегментами приёмов пищи,
 *  Daily Diet Goal — вертикальные шкалы потребления против цели.
 */
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Header(state) }
        item { SectionTitle("Chef recommendation", "with lemon butter sauce") }
        item { RecommendationRow(state.recommendations) }
        item { SectionTitle("Meal Plan", "balanced plan for the week") }
        item { WeekPlanRow(state.weekPlan) }
        item { DailyDietGoalCard(state.eatenKcal, state.eatenMacros, state.goal) }
    }
}

@Composable
private fun Header(state: HomeUiState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Аватар с инициалом
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) { Text("M", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Welcome back,", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Maria", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
            Text("сегодня", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("see all", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary)
        }
        Text(subtitle, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Лента карточек рекомендаций, как «Chef recommendation» на макете. */
@Composable
private fun RecommendationRow(recs: List<Recommendation>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(recs, key = { _, r -> r.dish.id }) { index, rec ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }
            val alpha by animateFloatAsState(if (visible) 1f else 0f,
                tween(300, delayMillis = index * 60), label = "appear")
            Card(
                modifier = Modifier
                    .width(190.dp)
                    .alpha(alpha),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(14.dp)) {
                    // «Фото» блюда — цветной круг с эмодзи-заглушкой (картинки подтянутся из сети)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) { Text("🍳", fontSize = 28.sp) }
                    Spacer(Modifier.height(10.dp))
                    Text(rec.dish.name, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text(
                        "${rec.dish.kcal} ккал · Б ${rec.dish.protein.toInt()} Ж ${rec.dish.fat.toInt()} У ${rec.dish.carbs.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { /* добавление в дневник — см. конструктор */ },
                        modifier = Modifier.fillMaxWidth().height(34.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) { Text("Add to diary", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}

/** 7 дней плана с сегментами завтрак/обед/ужин — цветные полоски как на макете. */
@Composable
private fun WeekPlanRow(plan: List<PlanDay>) {
    val today = LocalDate.now()
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(plan, key = { it.date.toEpochDay() }) { day ->
            val isToday = day.date == today
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.width(92.dp)
            ) {
                Column(
                    Modifier.padding(vertical = 14.dp, horizontal = 8.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("ru")).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    // Сегменты приёмов пищи: высота ~ доля ккал
                    MealSegment(day.breakfastKcal, MaterialTheme.colorScheme.tertiary, isToday)
                    MealSegment(day.lunchKcal, MaterialTheme.colorScheme.secondary, isToday)
                    MealSegment(day.dinnerKcal, MaterialTheme.colorScheme.primary, isToday)
                    Spacer(Modifier.height(6.dp))
                    Text("${day.totalKcal.toInt()} ккал",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant)
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
            .background(if (isToday) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else color)
    )
}

/** «Daily Diet Goal»: вертикальные шкалы kcal/жиры/углеводы/белки как на макете. */
@Composable
private fun DailyDietGoalCard(eatenKcal: Float, eaten: Macros, goal: com.nutri.app.core.domain.model.Goal?) {
    Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            GoalBar("ккал", eatenKcal, (goal?.dailyKcal ?: 0).toFloat(),
                MaterialTheme.colorScheme.tertiary)
            GoalBar("жиры", eaten.fat, (goal?.dailyFat ?: 0).toFloat(),
                MaterialTheme.colorScheme.primary)
            GoalBar("углев.", eaten.carbs, (goal?.dailyCarbs ?: 0).toFloat(),
                MaterialTheme.colorScheme.secondary)
            GoalBar("белки", eaten.protein, (goal?.dailyProtein ?: 0).toFloat(),
                MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun GoalBar(label: String, value: Float, target: Float, color: androidx.compose.ui.graphics.Color) {
    val fraction = if (target > 0) (value / target).coerceIn(0f, 1f) else 0f
    val progress by animateFloatAsState(fraction, tween(600), label = "bar")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .width(26.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            // Заполнение снизу вверх
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height((120 * progress).dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(color)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text("${value.toInt()}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
