package com.nutri.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutri.app.core.domain.model.*
import com.nutri.app.core.domain.repository.AuthRepository
import com.nutri.app.core.domain.repository.DiaryRepository
import com.nutri.app.core.domain.repository.DishRepository
import com.nutri.app.core.domain.repository.GoalRepository
import com.nutri.app.core.domain.usecase.GetRecommendationsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek

/**
 * Главный экран (dashboard как в design.jpg):
 * — карточки «Chef recommendation» (подбор по цели),
 * — Meal Plan по дням недели с распределением по приёмам пищи,
 * — текущее потребление против цели (шкалы БЖУ).
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val goal: Goal? = null,
    val recommendations: List<Recommendation> = emptyList(),
    /** План недели: день -> суммарные ккал блюд, распределённых по приёмам. */
    val weekPlan: List<PlanDay> = emptyList(),
    val eatenKcal: Float = 0f,
    val eatenMacros: Macros = Macros(0f, 0f, 0f),
    val userName: String = ""
) {
    /** Имя для приветствия: email/телефон (имя по умолчанию) не показываем. */
    val greetingName: String
        get() = userName.trim().takeIf { it.isNotBlank() && '@' !in it && !it.startsWith("+") }
            ?: "Пользователь"
    val initial: String get() = greetingName.take(1).uppercase()
}

/** Один день плана: суммы БЖУ по приёмам пищи (для цветных сегментов карточки дня). */
data class PlanDay(
    val date: LocalDate,
    val breakfastKcal: Float,
    val lunchKcal: Float,
    val dinnerKcal: Float
) {
    val totalKcal: Float get() = breakfastKcal + lunchKcal + dinnerKcal
}

class HomeViewModel(
    private val dishRepository: DishRepository,
    private val goalRepository: GoalRepository,
    private val diaryRepository: DiaryRepository,
    private val authRepository: AuthRepository,
    private val getRecommendations: GetRecommendationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            authRepository.user.collect { user ->
                _uiState.update { it.copy(userName = user?.displayName ?: "") }
            }
        }
        viewModelScope.launch {
            // Цель + потребление текущего дня
            combine(goalRepository.goal, diaryRepository.observeDay(1, LocalDate.now().toString())) { goal, entries ->
                goal to entries
            }.collect { (goal, entries) ->
                val macros = MacroCalculatorBridge.sumEntries(entries)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        goal = goal,
                        eatenKcal = entries.sumOf { e -> e.kcal.toDouble() }.toFloat(),
                        eatenMacros = macros
                    )
                }
            }
        }
        viewModelScope.launch {
            // Рекомендации под цель (остаток калорий на обед/ужин — упрощённо 500 ккал)
            dishRepository.observeDishes().collect { dishes ->
                val recs = getRecommendations(
                    GetRecommendationsUseCase.Params(
                        maxKcal = 500,
                        proteinGoal = 25f,
                        fatGoal = 20f,
                        carbsGoal = 55f,
                        limit = 6
                    ),
                    dishes
                )
                _uiState.update { it.copy(recommendations = recs) }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(weekPlan = buildWeekPlan()) }
        }
    }

    /**
     * План недели: базовые блюда с хорошим score раскладываются на 7 дней,
     * как в макете (пн–вс, сегменты завтрак/обед/ужин).
     */
    private suspend fun buildWeekPlan(): List<PlanDay> {
        val dishes = dishRepository.observeDishes().first()
            .sortedByDescending { it.protein / (it.kcal.coerceAtLeast(1)) }
        if (dishes.isEmpty()) return emptyList()
        val monday = LocalDate.now().with(DayOfWeek.MONDAY)
        val perMeal = { fraction: Float, dish: Dish -> dish.kcal * fraction }
        return (0..6).map { offset ->
            val date = monday.plusDays(offset.toLong())
            val breakfast = dishes[(offset * 2) % dishes.size]
            val lunch = dishes[(offset * 2 + 1) % dishes.size]
            val dinner = dishes[(offset * 3 + 2) % dishes.size]
            PlanDay(
                date = date,
                breakfastKcal = perMeal(0.25f, breakfast),
                lunchKcal = perMeal(0.45f, lunch),
                dinnerKcal = perMeal(0.30f, dinner)
            )
        }
    }
}

/** Мост к чистым функциям домена (не тащит Android-зависимостей). */
private object MacroCalculatorBridge {
    fun sumEntries(entries: List<DiaryEntry>): Macros =
        entries.fold(Macros(0f, 0f, 0f)) { total, entry ->
            total + Macros(entry.protein, entry.fat, entry.carbs)
        }
}
