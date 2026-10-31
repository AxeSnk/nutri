package com.nutri.app.feature.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutri.app.core.domain.ai.AiRecommender
import com.nutri.app.core.domain.model.*
import com.nutri.app.core.domain.repository.DishRepository
import com.nutri.app.core.domain.usecase.GetRecommendationsUseCase
import com.nutri.app.core.domain.usecase.LogDishUseCase
import com.nutri.app.core.domain.util.MacroCalculator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Экран конструктора: два режима — готовые карточки и сборка из ингредиентов.
 * Единый Immutable-стейт через StateFlow; действия пользователя — события (методы on*).
 */
data class BuilderUiState(
    val isLoading: Boolean = false,
    val isSearchingIngredients: Boolean = false,
    val error: String? = null,
    // Режим 1: карточки блюд
    val dishes: List<Dish> = emptyList(),
    val recommendations: List<Recommendation> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val maxCookMinutes: Int? = null,
    val recommendationKcal: Int = 600,
    val recommendationProtein: Float = 30f,
    val recommendationFat: Float = 20f,
    val recommendationCarbs: Float = 55f,
    // Режим 2: сборка блюда
    val builderItems: List<BuilderItem> = emptyList(),
    val builderMacros: Macros = Macros(0f, 0f, 0f),
    val builderKcal: Float = 0f,
    val builderGrams: Float = 0f,
    // Общее
    val isOffline: Boolean = false,
    val isPremium: Boolean = false
)

/** Ингредиент в конструкторе: продукт + вес. Пересчёт мгновенный. */
data class BuilderItem(val ingredient: Ingredient, val grams: Float)

sealed interface BuilderAction {
    data class SelectTag(val tag: String) : BuilderAction
    data class SetCookTime(val minutes: Int?) : BuilderAction
    data class SetNutritionTargets(
        val kcal: Int,
        val protein: Float,
        val fat: Float,
        val carbs: Float
    ) : BuilderAction
    data class AddIngredient(val ingredient: Ingredient, val grams: Float) : BuilderAction
    data class RemoveIngredient(val index: Int) : BuilderAction
    data class LogDish(val dish: Dish, val mealType: MealType) : BuilderAction
    data class LogBuilt(val mealType: MealType) : BuilderAction
    object Refresh : BuilderAction
}

class BuilderViewModel(
    private val dishRepository: DishRepository,
    private val getRecommendations: GetRecommendationsUseCase,
    private val logDishUseCase: LogDishUseCase,
    private val aiRecommender: AiRecommender
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuilderUiState())
    val uiState: StateFlow<BuilderUiState> = _uiState.asStateFlow()
    private var cachedDishes: List<Dish> = emptyList()

    init {
        viewModelScope.launch {
            dishRepository.observeDishes().collect { dishes ->
                cachedDishes = dishes
                updateDishLists()
            }
        }
        refresh()
    }

    /** Единая точка входа для UI-событий. */
    fun onAction(action: BuilderAction) {
        when (action) {
            is BuilderAction.SelectTag -> toggleTag(action.tag)
            is BuilderAction.SetCookTime -> {
                _uiState.update { it.copy(maxCookMinutes = action.minutes) }
                viewModelScope.launch { updateDishLists() }
            }
            is BuilderAction.SetNutritionTargets -> {
                _uiState.update {
                    it.copy(
                        recommendationKcal = action.kcal,
                        recommendationProtein = action.protein,
                        recommendationFat = action.fat,
                        recommendationCarbs = action.carbs
                    )
                }
                viewModelScope.launch { updateDishLists() }
            }
            is BuilderAction.AddIngredient -> addItem(action.ingredient, action.grams)
            is BuilderAction.RemoveIngredient -> _uiState.update { st ->
                if (action.index !in st.builderItems.indices) st
                else builderState(st.builderItems.filterIndexed { index, _ -> index != action.index }, st)
            }
            is BuilderAction.LogDish -> log(action.dish, action.mealType, grams = 100f)
            is BuilderAction.LogBuilt -> logBuilt(action.mealType)
            BuilderAction.Refresh -> refresh()
        }
    }

    private fun toggleTag(tag: String) {
        _uiState.update {
            it.copy(selectedTags = if (tag in it.selectedTags) it.selectedTags - tag else it.selectedTags + tag)
        }
        viewModelScope.launch { updateDishLists() }
    }

    /**
     * Offline-first: немедленно показываем кэш, сеть тихо обновляет данные в фоне.
     * Рекомендации пересчитываются локально — быстро, без сети.
     */
    private fun refresh() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = dishRepository.refreshDishes()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOffline = result.isFailure,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private suspend fun updateDishLists() {
        val state = _uiState.value
        val filtered = cachedDishes.filter { dish ->
            state.selectedTags.all { it in dish.tags } &&
                (state.maxCookMinutes?.let { dish.cookMinutes <= it } ?: true)
        }
        val recommendations = getRecommendations(
            GetRecommendationsUseCase.Params(
                maxKcal = state.recommendationKcal,
                proteinGoal = state.recommendationProtein,
                fatGoal = state.recommendationFat,
                carbsGoal = state.recommendationCarbs,
                tags = state.selectedTags,
                maxCookMinutes = state.maxCookMinutes
            ),
            filtered
        )
        _uiState.update {
            it.copy(isLoading = false, dishes = filtered, recommendations = recommendations)
        }
    }

    private fun addItem(ingredient: Ingredient, grams: Float) {
        if (!grams.isFinite() || grams <= 0f) return
        _uiState.update { st ->
            val items = st.builderItems + BuilderItem(ingredient, grams)
            builderState(items, st)
        }
    }

    private fun builderState(items: List<BuilderItem>, previous: BuilderUiState): BuilderUiState {
        val macros = MacroCalculator.sum(items.map { it.grams to it.ingredient.macrosPer100 })
        return previous.copy(
            builderItems = items,
            builderMacros = macros,
            builderKcal = macros.kcalPer100,
            builderGrams = items.sumOf { it.grams.toDouble() }.toFloat()
        )
    }

    private fun log(dish: Dish, mealType: MealType, grams: Float) {
        viewModelScope.launch {
            runCatching {
                logDishUseCase(dish, grams, userId = 1, date = LocalDate.now().toString(), mealType)
            }.onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private fun logBuilt(mealType: MealType) {
        val st = _uiState.value
        if (st.builderItems.isEmpty() || st.builderGrams <= 0f) return
        val per100 = st.builderMacros * (100f / st.builderGrams)
        val built = Dish(
            id = "built_${System.currentTimeMillis()}",
            name = "Моё блюдо (" + st.builderItems.joinToString { it.ingredient.name } + ")",
            kcal = (st.builderKcal * 100f / st.builderGrams).toInt(),
            protein = per100.protein,
            fat = per100.fat,
            carbs = per100.carbs
        )
        log(built, mealType, grams = st.builderGrams)
    }

    /** Поиск ингредиента через репозиторий (локально -> OpenFoodFacts). */
    private val _ingredientResults = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredientResults: StateFlow<List<Ingredient>> = _ingredientResults.asStateFlow()
    private var ingredientSearchJob: Job? = null

    fun searchIngredients(query: String) {
        ingredientSearchJob?.cancel()
        if (query.length < 2) {
            _ingredientResults.value = emptyList()
            _uiState.update { it.copy(isSearchingIngredients = false, error = null) }
            return
        }
        ingredientSearchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearchingIngredients = true, error = null) }
            dishRepository.searchIngredient(query).fold(
                onSuccess = { results -> _ingredientResults.value = results },
                onFailure = { error -> _uiState.update { it.copy(error = error.message) } }
            )
            _uiState.update { it.copy(isSearchingIngredients = false) }
        }
    }
}
