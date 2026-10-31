package com.nutri.app.core.domain.util

import com.nutri.app.core.domain.model.Macros

/**
 * Чистые функции расчётов БЖУ и калорий. Никаких Android-зависимостей —
 * тестируется обычным JUnit-тестом без эмулятора.
 */
object MacroCalculator {

    /** Калории для произвольного веса продукта. */
    fun kcalFor(grams: Float, per100: Macros): Float = per100.kcalPer100 * grams / 100f

    /** Масштабирует указанную калорийность с упаковки/карточки на вес порции. */
    fun kcalFor(grams: Float, kcalPer100: Float): Float = kcalPer100 * grams / 100f

    /** Макросы для произвольного веса продукта (г белка/жира/углеводов). */
    fun macrosFor(grams: Float, per100: Macros): Macros = per100 * (grams / 100f)

    /**
     * Сумма макросов набора ингредиентов: каждый со своим весом.
     * Используется конструктором блюд при добавлении/удалении ингредиента.
     */
    fun sum(items: List<Pair<Float, Macros>>): Macros =
        items.fold(Macros(0f, 0f, 0f)) { acc, (grams, per100) -> acc + macrosFor(grams, per100) }

    /** Отклонение факта от цели по каждому макросу (positive = перебор). */
    fun deviation(actual: Macros, goal: Macros): Macros =
        Macros(actual.protein - goal.protein, actual.fat - goal.fat, actual.carbs - goal.carbs)

    /** Дефицит (positive) или профицит (negative) калорий за день. */
    fun kcalBalance(eatenKcal: Float, goalKcal: Int): Float = goalKcal - eatenKcal

    /**
     * Валидация цели: калории должны примерно соответствовать сумме БЖУ (±10%).
     * Возвращает список ошибок валидации, пустой = цель корректна.
     */
    fun validateGoal(kcal: Int, protein: Int, fat: Int, carbs: Int): List<String> {
        val errors = mutableListOf<String>()
        if (kcal !in 800..6000) errors += "Калории должны быть в диапазоне 800–6000"
        val fromMacros = protein * 4 + fat * 9 + carbs * 4
        if (fromMacros == 0) errors += "Заполните БЖУ"
        else {
            val diff = kotlin.math.abs(fromMacros - kcal) / kcal.toFloat()
            if (diff > 0.10f) errors += "Сумма БЖУ (${fromMacros} ккал) сильно расходится с целью $kcal ккал"
        }
        return errors
    }
}
