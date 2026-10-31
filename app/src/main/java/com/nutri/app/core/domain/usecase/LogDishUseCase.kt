package com.nutri.app.core.domain.usecase

import com.nutri.app.core.domain.model.DiaryEntry
import com.nutri.app.core.domain.model.Dish
import com.nutri.app.core.domain.model.Macros
import com.nutri.app.core.domain.repository.DiaryRepository
import com.nutri.app.core.domain.util.MacroCalculator

/** Добавляет порцию блюда в дневник, пересчитывая макросы под вес порции. */
class LogDishUseCase(private val diaryRepository: DiaryRepository) {
    suspend operator fun invoke(
        dish: Dish,
        grams: Float,
        userId: Long,
        date: String,
        mealType: com.nutri.app.core.domain.model.MealType
    ) {
        val macros = MacroCalculator.macrosFor(grams, dish.macros)
        val kcal = MacroCalculator.kcalFor(grams, dish.kcal.toFloat())
        diaryRepository.addEntry(
            DiaryEntry(
                userId = userId,
                date = date,
                mealType = mealType,
                dishId = dish.id,
                dishName = dish.name,
                grams = grams,
                kcal = kcal,
                protein = macros.protein,
                fat = macros.fat,
                carbs = macros.carbs
            )
        )
    }
}
