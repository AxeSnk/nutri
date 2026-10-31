package com.nutri.app.feature.builder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutri.app.core.domain.model.Dish
import com.nutri.app.core.domain.model.Ingredient
import com.nutri.app.core.domain.model.MealType
import com.nutri.app.core.domain.model.Recommendation

/**
 * Конструктор питания — два режима в одном экране:
 *  TAB_DISHES: готовые карточки блюд + фильтры (теги, время приготовления)
 *  TAB_CUSTOM: сборка блюда из ингредиентов с live-пересчётом БЖУ
 */
@Composable
fun BuilderScreen(viewModel: BuilderViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Готовые блюда") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Собрать своё") })
        }
        when (tab) {
            0 -> DishesTab(state, viewModel::onAction)
            1 -> CustomTab(state, viewModel::onAction, viewModel::searchIngredients,
                viewModel.ingredientResults.collectAsStateWithLifecycle().value)
        }
    }
}

// ---------- Режим 1: карточки блюд ----------

@Composable
private fun DishesTab(state: BuilderUiState, onAction: (BuilderAction) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        // Блок фильтров: теги + время
        item {
            FilterChips(
                selectedTags = state.selectedTags,
                cookMinutes = state.maxCookMinutes,
                kcal = state.recommendationKcal,
                protein = state.recommendationProtein,
                fat = state.recommendationFat,
                carbs = state.recommendationCarbs,
                onTag = { onAction(BuilderAction.SelectTag(it)) },
                onCookTime = { onAction(BuilderAction.SetCookTime(it)) },
                onNutritionTargets = { kcal, protein, fat, carbs ->
                    onAction(BuilderAction.SetNutritionTargets(kcal, protein, fat, carbs))
                }
            )
            Spacer(Modifier.height(12.dp))
        }
        if (state.isOffline) item {
            Text("Оффлайн: показаны сохранённые блюда", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(8.dp))
        }
        state.error?.let { message ->
            item {
                Text(message, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
        // Рекомендации — анимированное появление карточек
        itemsIndexed(state.recommendations, key = { _, r -> r.dish.id }) { index, rec ->
            AnimatedDishCard(rec.dish, index, extra = rec.reasons.joinToString(" · "),
                onLog = { onAction(BuilderAction.LogDish(rec.dish, MealType.LUNCH)) })
            Spacer(Modifier.height(8.dp))
        }
        val recommendedIds = state.recommendations.mapTo(mutableSetOf()) { it.dish.id }
        itemsIndexed(state.dishes.filterNot { it.id in recommendedIds }, key = { _, d -> d.id }) { index, dish ->
            AnimatedDishCard(dish, index,
                onLog = { onAction(BuilderAction.LogDish(dish, MealType.LUNCH)) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterChips(
    selectedTags: Set<String>,
    cookMinutes: Int?,
    kcal: Int,
    protein: Float,
    fat: Float,
    carbs: Float,
    onTag: (String) -> Unit,
    onCookTime: (Int?) -> Unit,
    onNutritionTargets: (Int, Float, Float, Float) -> Unit
) {
    var showNutritionTargets by rememberSaveable { mutableStateOf(false) }
    val tagLabels = listOf("vegetarian" to "Вегетарианское", "gluten_free" to "Без глютена", "high_protein" to "Много белка")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tagLabels.forEach { (tag, label) ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = { onTag(tag) },
                label = { Text(label) }
            )
        }
        listOf(15 to "≤15 мин", 30 to "≤30 мин").forEach { (minutes, label) ->
            FilterChip(
                selected = cookMinutes == minutes,
                onClick = { onCookTime(if (cookMinutes == minutes) null else minutes) },
                label = { Text(label) }
            )
        }
    }
    TextButton(onClick = { showNutritionTargets = !showNutritionTargets }) {
        Text(if (showNutritionTargets) "Скрыть цели подбора" else "Настроить калории и БЖУ")
    }
    AnimatedVisibility(showNutritionTargets) {
        Column {
            Text("Не более $kcal ккал на блюдо", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = kcal.toFloat(),
                onValueChange = {
                    onNutritionTargets(it.toInt(), protein, fat, carbs)
                },
                valueRange = 100f..1000f,
                steps = 17
            )
            MacroTargetSlider("Белки", protein) {
                onNutritionTargets(kcal, it, fat, carbs)
            }
            MacroTargetSlider("Жиры", fat) {
                onNutritionTargets(kcal, protein, it, carbs)
            }
            MacroTargetSlider("Углеводы", carbs) {
                onNutritionTargets(kcal, protein, fat, it)
            }
        }
    }
}

@Composable
private fun MacroTargetSlider(label: String, grams: Float, onChange: (Float) -> Unit) {
    Text("$label: ${grams.toInt()} г", style = MaterialTheme.typography.labelLarge)
    Slider(value = grams, onValueChange = onChange, valueRange = 0f..100f, steps = 19)
}

/** Карточка блюда с мягким появлением (fade+slide по индексу — умеренная анимация). */
@Composable
private fun AnimatedDishCard(dish: Dish, index: Int, extra: String = "", onLog: () -> Unit) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 250, delayMillis = index * 40),
        label = "cardAppear"
    )
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(dish.name, style = MaterialTheme.typography.titleMedium)
            if (extra.isNotBlank()) Text(extra, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text("${dish.kcal} ккал · Б ${dish.protein} / Ж ${dish.fat} / У ${dish.carbs}",
                style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            AssistChip(onClick = onLog, label = { Text("Записать в дневник") })
        }
    }
}

// ---------- Режим 2: сборка блюда из ингредиентов ----------

@Composable
private fun CustomTab(
    state: BuilderUiState,
    onAction: (BuilderAction) -> Unit,
    onSearch: (String) -> Unit,
    ingredientResults: List<Ingredient>
) {
    var query by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Живой пересчёт: kcal + БЖУ сборки
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Итого: ${state.builderKcal.toInt()} ккал", style = MaterialTheme.typography.titleLarge)
                Text("Б ${state.builderMacros.protein.toInt()} · Ж ${state.builderMacros.fat.toInt()} · У ${state.builderMacros.carbs.toInt()}",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
        state.error?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }
        if (state.isSearchingIngredients) {
            LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it; onSearch(it) },
            label = { Text("Найти ингредиент (гречка, курица…)") },
            modifier = Modifier.fillMaxWidth()
        )
        // Результаты поиска
        ingredientResults.take(5).forEach { ing ->
            ListItem(
                headlineContent = { Text(ing.name) },
                supportingContent = { Text("${ing.macrosPer100.kcalPer100.toInt()} ккал / 100 г") },
                trailingContent = {
                    TextButton(onClick = { onAction(BuilderAction.AddIngredient(ing, 100f)) }) { Text("+100 г") }
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        Text("Состав блюда", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.weight(1f)) {
            itemsIndexed(state.builderItems) { index, item ->
                ListItem(
                    headlineContent = { Text(item.ingredient.name) },
                    supportingContent = { Text("${item.grams.toInt()} г") },
                    trailingContent = {
                        TextButton(onClick = { onAction(BuilderAction.RemoveIngredient(index)) }) { Text("✕") }
                    }
                )
            }
        }
        Button(
            onClick = { onAction(BuilderAction.LogBuilt(MealType.LUNCH)) },
            enabled = state.builderItems.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Записать блюдо в дневник") }
    }
}
