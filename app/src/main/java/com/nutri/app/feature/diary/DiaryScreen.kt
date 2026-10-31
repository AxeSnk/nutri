package com.nutri.app.feature.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutri.app.core.domain.model.MealType
import com.nutri.app.core.domain.usecase.ObserveDailyProgressUseCase.Progress

/** Дневник: разбивка по приёмам пищи, дефицит/профицит, отклонения БЖУ, график по дням. */
@Composable
fun DiaryScreen(viewModel: DiaryViewModel) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val p = progress ?: return

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        // Баланс калорий
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        if (p.kcalBalance >= 0) "Дефицит: ${p.kcalBalance.toInt()} ккал"
                        else "Профицит: ${(-p.kcalBalance).toInt()} ккал",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (p.kcalBalance >= 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                    Text("Съедено: ${p.eatenKcal.toInt()} ккал", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    // Отклонения по БЖУ (positive = перебор)
                    Text("Белки: ${p.eatenMacros.protein.toInt()} г (${dev(p.macrosDeviation.protein)})",
                        style = MaterialTheme.typography.bodySmall)
                    Text("Жиры: ${p.eatenMacros.fat.toInt()} г (${dev(p.macrosDeviation.fat)})",
                        style = MaterialTheme.typography.bodySmall)
                    Text("Углеводы: ${p.eatenMacros.carbs.toInt()} г (${dev(p.macrosDeviation.carbs)})",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        // Столбчатый график по дням (простой Canvas-free вариант на Box)
        item {
            Text("Последние 7 дней", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            WeekBarChart(p.history)
            Spacer(Modifier.height(16.dp))
        }
        // Разбивка по приёмам пищи
        MealType.entries.forEach { meal ->
            val entries = p.entries.filter { it.mealType == meal }
            item {
                Text(meal.label(), style = MaterialTheme.typography.titleMedium)
                if (entries.isEmpty()) Text("— ничего —", style = MaterialTheme.typography.bodySmall)
                else entries.forEach { e ->
                    ListItem(
                        headlineContent = { Text(e.dishName) },
                        supportingContent = { Text("${e.grams.toInt()} г · Б ${e.protein.toInt()} / Ж ${e.fat.toInt()} / У ${e.carbs.toInt()}") },
                        trailingContent = { Text("${e.kcal.toInt()} ккал") }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun dev(value: Float) = when {
    value > 0f -> "+${value.toInt()} г"
    value < 0f -> "${value.toInt()} г"
    else -> "норма"
}

private fun MealType.label() = when (this) {
    MealType.BREAKFAST -> "Завтрак"
    MealType.LUNCH -> "Обед"
    MealType.DINNER -> "Ужин"
    MealType.SNACK -> "Перекус"
}

/** Простой столбчатый график: высота колонки пропорциональна ккал дня. */
@Composable
private fun WeekBarChart(history: Map<String, Float>) {
    val max = (history.maxOfOrNull { it.value } ?: 1f).coerceAtLeast(1f)
    Row(Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom) {
        history.entries.forEach { (date, kcal) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((kcal / max * 100).dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(date.takeLast(2), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
