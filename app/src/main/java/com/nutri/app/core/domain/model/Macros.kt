package com.nutri.app.core.domain.model

/** Единица измерения ингредиента. */
enum class Unit { GRAM, MILLILITER, PIECE }

/**
 * Макронутриенты на 100 г/мл продукта.
 * Все расчёты ведутся в этой нормализованной форме — это ключевое решение,
 * благодаря которому пересчёт порций сводится к одному множителю (граммы / 100).
 */
data class Macros(
    val protein: Float,   // г на 100 г
    val fat: Float,       // г на 100 г
    val carbs: Float      // г на 100 г
) {
    /** Калорийность на 100 г по формуле 4/9/4 (Atwater). Чистая функция — без Android. */
    val kcalPer100: Float get() = protein * 4f + fat * 9f + carbs * 4f

    operator fun plus(other: Macros) = Macros(protein + other.protein, fat + other.fat, carbs + other.carbs)
    operator fun times(scale: Float) = Macros(protein * scale, fat * scale, carbs * scale)
}
