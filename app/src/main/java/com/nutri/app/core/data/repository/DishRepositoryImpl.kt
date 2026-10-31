package com.nutri.app.core.data.repository

import com.nutri.app.core.data.local.*
import com.nutri.app.core.data.remote.OpenFoodFactsApi
import com.nutri.app.core.domain.model.*
import com.nutri.app.core.domain.repository.DishRepository
import com.nutri.app.core.domain.repository.DiaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// ---------- Mappers (внутренние, для слоя data) ----------

private fun DishEntity.toDomain() = Dish(
    id = id, name = name, imageUrl = imageUrl, kcal = kcal,
    protein = protein, fat = fat, carbs = carbs,
    tags = DishEntity.tagsToSet(tags), cookMinutes = cookMinutes, isFromRemote = isFromRemote
)

private fun Dish.toEntity(now: Long) = DishEntity(
    id = id, name = name, imageUrl = imageUrl, kcal = kcal, protein = protein, fat = fat, carbs = carbs,
    tags = DishEntity.setToTags(tags), cookMinutes = cookMinutes, isFromRemote = isFromRemote, updatedAt = now
)

private fun DiaryEntryEntity.toDomain() = DiaryEntry(
    id = id, userId = userId, date = date,
    mealType = MealType.valueOf(mealType), dishId = dishId, dishName = dishName,
    grams = grams, kcal = kcal, protein = protein, fat = fat, carbs = carbs
)

private fun IngredientEntity.toDomain() = Ingredient(
    id = id, name = name, unit = com.nutri.app.core.domain.model.Unit.valueOf(unit),
    macrosPer100 = Macros(proteinPer100, fatPer100, carbsPer100)
)

/** Offline-first: сетевой продукт обогащает локальную базу, но не обязателен. */
private fun OffProductDto.toIngredient() = Ingredient(
    id = "off_${id}",
    name = name ?: "Продукт",
    unit = com.nutri.app.core.domain.model.Unit.GRAM,
    macrosPer100 = Macros(nutriments?.protein ?: 0f, nutriments?.fat ?: 0f, nutriments?.carbs ?: 0f)
)

// тип-алиас, чтобы не тащить DTO в маппер выше
private typealias OffProductDto = com.nutri.app.core.data.remote.OffProduct

/**
 * Репозиторий блюд: сначала кэш Room, затем сеть.
 * Сеть только обогащает кэш — UI всегда читает из Room, поэтому оффлайн работает полностью.
 */
class DishRepositoryImpl(
    private val db: NutriDatabase,
    private val api: OpenFoodFactsApi
) : DishRepository {

    override fun observeDishes(): Flow<List<Dish>> =
        db.dishDao().observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun refreshDishes(tags: Set<String>, maxKcal: Int?): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Сеть: тянем популярные продукты OpenFoodFacts и превращаем в карточки
                val response = api.searchProducts(query = "")
                val now = System.currentTimeMillis()
                val remoteDishes = response.products
                    .filter { it.nutriments?.kcal != null && !it.name.isNullOrBlank() }
                    .map { p ->
                        Dish(
                            id = "off_${p.id}",
                            name = p.name!!,
                            imageUrl = p.imageUrl,
                            kcal = p.nutriments!!.kcal!!.toInt(),
                            protein = p.nutriments.protein ?: 0f,
                            fat = p.nutriments.fat ?: 0f,
                            carbs = p.nutriments.carbs ?: 0f,
                            tags = emptySet(),
                            isFromRemote = true
                        )
                    }
                // 2. Кэш: upsert одним батчем — быстро, одна транзакция
                db.dishDao().upsertAll(remoteDishes.map { it.toEntity(now) })
                db.dishDao().deleteStaleRemote(now - 7 * 24 * 60 * 60 * 1000L)
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Оффлайн — не ошибка для пользователя: кэш уже отдаёт данные
                Result.failure(e)
            }
        }

    override suspend fun searchIngredient(query: String): Result<List<Ingredient>> =
        withContext(Dispatchers.IO) {
            val local = db.ingredientDao().search(query).map { it.toDomain() }
            if (local.isNotEmpty()) return@withContext Result.success(local)
            try {
                val remote = api.searchProducts(query).products
                    .filter { it.nutriments != null && !it.name.isNullOrBlank() }
                    .map { p ->
                        IngredientEntity(
                            id = "off_${p.id}", name = p.name!!,
                            proteinPer100 = p.nutriments!!.protein ?: 0f,
                            fatPer100 = p.nutriments.fat ?: 0f,
                            carbsPer100 = p.nutriments.carbs ?: 0f,
                            unit = com.nutri.app.core.domain.model.Unit.GRAM.name
                        )
                    }
                db.ingredientDao().upsertAll(remote)
                Result.success(remote.map { it.toDomain() })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

/** Репозиторий дневника: всё локально, сеть не нужна. */
class DiaryRepositoryImpl(private val db: NutriDatabase) : DiaryRepository {

    override fun observeDay(userId: Long, date: String): Flow<List<DiaryEntry>> =
        db.diaryDao().observeDay(userId, date).map { list -> list.map { it.toDomain() } }

    override suspend fun addEntry(entry: DiaryEntry) {
        withContext(Dispatchers.IO) {
            db.diaryDao().insert(
                DiaryEntryEntity(
                    userId = entry.userId, date = entry.date, mealType = entry.mealType.name,
                    dishId = entry.dishId, dishName = entry.dishName, grams = entry.grams,
                    kcal = entry.kcal, protein = entry.protein, fat = entry.fat, carbs = entry.carbs
                )
            )
        }
    }

    override suspend fun removeEntry(id: Long) = withContext(Dispatchers.IO) { db.diaryDao().delete(id) }

    override fun observeDailyTotals(userId: Long, fromDate: String): Flow<Map<String, Float>> =
        db.diaryDao().observeDailyTotals(userId, fromDate).map { rows ->
            rows.associate { it.date to it.total }
        }
}
