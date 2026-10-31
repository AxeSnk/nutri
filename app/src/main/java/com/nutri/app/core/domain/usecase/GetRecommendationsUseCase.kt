package com.nutri.app.core.domain.usecase

import com.nutri.app.core.domain.model.*
import com.nutri.app.core.domain.repository.DishRepository

/**
 * Подбор рекомендаций: фильтрация карточек блюд по параметрам
 * и сортировка по «близости» к остатку калорий/БЖУ цели.
 * Чистая доменная логика, не знает про Room/Retrofit/UI.
 */
class GetRecommendationsUseCase(
    private val dishRepository: DishRepository
) {
    data class Params(
        val maxKcal: Int? = null,          // остаток калорий на день
        val proteinGoal: Float? = null,    // остаток белка
        val fatGoal: Float? = null,        // остаток жиров
        val carbsGoal: Float? = null,      // остаток углеводов
        val tags: Set<String> = emptySet(),// вегетарианское, без глютена…
        val maxCookMinutes: Int? = null,   // время приготовления
        val limit: Int = 10
    )

    suspend operator fun invoke(params: Params, allDishes: List<Dish>): List<Recommendation> {
        return allDishes
            // 1. Жёсткие фильтры — блюдо должно удовлетворять всем заданным ограничениям
            .filter { dish ->
                params.tags.all { tag -> tag in dish.tags }
            }
            .filter { dish -> params.maxKcal?.let { dish.kcal <= it } ?: true }
            .filter { dish -> params.maxCookMinutes?.let { dish.cookMinutes <= it } ?: true }
            // 2. Оценка: чем ближе ккал блюда к запрошенным, тем выше score
            .map { dish ->
                val kcalFit = params.maxKcal?.let { target ->
                    1f - (kotlin.math.abs(target - dish.kcal).toFloat() /
                        kotlin.math.max(target, 1)).coerceIn(0f, 1f)
                }
                val macroFits = listOfNotNull(
                    params.proteinGoal?.let { target -> macroFit(dish.protein, target) },
                    params.fatGoal?.let { target -> macroFit(dish.fat, target) },
                    params.carbsGoal?.let { target -> macroFit(dish.carbs, target) }
                )
                val scoreParts = listOfNotNull(kcalFit) + macroFits
                val score = scoreParts.average().toFloat().takeIf { scoreParts.isNotEmpty() } ?: 0.5f

                // Подбор причин для подсказки пользователю
                val reasons = buildList {
                    params.tags.intersect(dish.tags).forEach { add(tagLabel(it)) }
                    if (params.maxKcal != null && dish.kcal <= params.maxKcal) add("вписывается в остаток калорий")
                    if (dish.cookMinutes in 1..15) add("быстрое приготовление")
                }

                Recommendation(dish, score = score, reasons)
            }
            // 3. Лучшие — сверху
            .sortedByDescending { it.score }
            .take(params.limit)
    }

    /** Человекочитаемые названия тегов для карточки рекомендации. */
    private fun macroFit(actual: Float, target: Float): Float =
        1f - (kotlin.math.abs(target - actual) / kotlin.math.max(target, 1f)).coerceIn(0f, 1f)

    private fun tagLabel(tag: String) = when (tag) {
        "vegetarian" -> "вегетарианское"
        "gluten_free" -> "без глютена"
        "high_protein" -> "много белка"
        "low_carb" -> "мало углеводов"
        "vegan" -> "веганское"
        else -> tag
    }
}
