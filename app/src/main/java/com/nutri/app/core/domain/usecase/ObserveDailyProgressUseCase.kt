package com.nutri.app.core.domain.usecase

import com.nutri.app.core.domain.model.*
import com.nutri.app.core.domain.repository.DiaryRepository
import com.nutri.app.core.domain.repository.GoalRepository
import com.nutri.app.core.domain.util.MacroCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Агрегирует дневник + цель в состояние прогресса для экрана дневника.
 * Считает дефицит/профицит калорий и отклонения по БЖУ.
 */
class ObserveDailyProgressUseCase(
    private val diaryRepository: DiaryRepository,
    private val goalRepository: GoalRepository
) {
    data class Progress(
        val entries: List<DiaryEntry>,
        val goal: Goal?,
        val eatenKcal: Float,
        val eatenMacros: Macros,
        /** positive = дефицит (осталось съесть), negative = профицит */
        val kcalBalance: Float,
        val macrosDeviation: Macros,
        /** История по дням для графика: дата -> съедено ккал. */
        val history: Map<String, Float>
    )

    operator fun invoke(userId: Long, date: String): Flow<Progress> =
        combine(
            diaryRepository.observeDay(userId, date),
            goalRepository.goal,
            diaryRepository.observeDailyTotals(userId, fromDate = date.takeLastFromWeek())
        ) { entries, goal, history ->
            val macros = entries.fold(Macros(0f, 0f, 0f)) { total, entry ->
                total + Macros(entry.protein, entry.fat, entry.carbs)
            }
            val eatenKcal = entries.sumOf { it.kcal.toDouble() }.toFloat()
            val goalMacros = goal?.let { Macros(it.dailyProtein.toFloat(), it.dailyFat.toFloat(), it.dailyCarbs.toFloat()) }
                ?: Macros(0f, 0f, 0f)
            Progress(
                entries = entries,
                goal = goal,
                eatenKcal = eatenKcal,
                eatenMacros = macros,
                kcalBalance = MacroCalculator.kcalBalance(eatenKcal, goal?.dailyKcal ?: 0),
                macrosDeviation = goal?.let { MacroCalculator.deviation(macros, goalMacros) } ?: Macros(0f, 0f, 0f),
                history = history
            )
        }

    /** Датa 7 дней назад (упрощённо, по строке ISO). */
    private fun String.takeLastFromWeek(): String {
        // date уже в формате yyyy-MM-dd; для MVP сдвигаем на 6 дней через java.time (доступен с API 26)
        return try {
            val d = java.time.LocalDate.parse(this).minusDays(6).toString()
            d
        } catch (e: Exception) { this }
    }
}
