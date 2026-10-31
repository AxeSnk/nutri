package com.nutri.app.core.domain.repository

import com.nutri.app.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DishRepository {
    /** Локальный поток блюд — источник для UI, обновляется после сетевой синхронизации. */
    fun observeDishes(): Flow<List<Dish>>

    /** Offline-first: сразу отдаёт кэш, затем обновляет его из сети. */
    suspend fun refreshDishes(tags: Set<String> = emptySet(), maxKcal: Int? = null): Result<Unit>

    suspend fun searchIngredient(query: String): Result<List<Ingredient>>
}

interface DiaryRepository {
    fun observeDay(userId: Long, date: String): Flow<List<DiaryEntry>>
    suspend fun addEntry(entry: DiaryEntry)
    suspend fun removeEntry(id: Long)
    /** История по дням для столбчатого графика (kcal по дате). */
    fun observeDailyTotals(userId: Long, fromDate: String): Flow<Map<String, Float>>
}

interface GoalRepository {
    val goal: Flow<Goal?>
    suspend fun saveGoal(goal: Goal)
}

interface AuthRepository {
    val user: Flow<User?>
    suspend fun loginWithEmail(email: String): Result<User>
    suspend fun loginWithPhone(phone: String): Result<User>
    suspend fun updateDisplayName(name: String)
    suspend fun logout()
}
