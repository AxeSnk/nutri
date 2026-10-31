package com.nutri.app.core.domain.model

data class User(
    val id: Long = 0,
    /** Email или телефон — один из них всегда заполнен. */
    val email: String? = null,
    val phone: String? = null,
    val displayName: String = "",
    val isPremium: Boolean = false
)

/** Суточная цель пользователя. */
data class Goal(
    val userId: Long,
    val dailyKcal: Int,
    val dailyProtein: Int,   // г
    val dailyFat: Int,       // г
    val dailyCarbs: Int      // г
)

enum class MealType { BREAKFAST, LUNCH, DINNER, SNACK }

/** Ингредиент — базовая единица продуктов (локальная база + OpenFoodFacts). */
data class Ingredient(
    val id: String,
    val name: String,
    val macrosPer100: Macros,
    val unit: Unit = Unit.GRAM
)

/** Готовое блюдо (карточка) — набор ингредиентов или фиксированные макросы. */
data class Dish(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val kcal: Int,
    val protein: Float,
    val fat: Float,
    val carbs: Float,
    val tags: Set<String> = emptySet(),          // "vegetarian", "gluten_free", "high_protein"…
    val cookMinutes: Int = 0,                    // для фильтра по времени приготовления
    val isFromRemote: Boolean = false
) {
    val macros: Macros get() = Macros(protein, fat, carbs)
}

/** Позиция в дневнике: блюдо или собранное блюдо, съеденное в приём пищи. */
data class DiaryEntry(
    val id: Long = 0,
    val userId: Long,
    val date: String,          // ISO yyyy-MM-dd — индекс для выборки дня
    val mealType: MealType,
    val dishId: String,
    val dishName: String,
    val grams: Float,
    val kcal: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float
)

/** Рекомендация — блюдо + причина попадания в подборку. */
data class Recommendation(
    val dish: Dish,
    /** Насколько блюдо подходит под цель (0..1), для сортировки. */
    val score: Float,
    val reasons: List<String>  // "вписывается в остаток калорий", "вегетарианское"
)
