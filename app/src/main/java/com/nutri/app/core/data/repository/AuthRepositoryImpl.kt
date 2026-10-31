package com.nutri.app.core.data.repository

import com.nutri.app.core.data.local.GoalEntity
import com.nutri.app.core.data.local.NutriDatabase
import com.nutri.app.core.data.local.UserEntity
import com.nutri.app.core.domain.model.Goal
import com.nutri.app.core.domain.model.User
import com.nutri.app.core.domain.repository.AuthRepository
import com.nutri.app.core.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private fun GoalEntity.toDomain() = Goal(userId, dailyKcal, dailyProtein, dailyFat, dailyCarbs)
private fun UserEntity.toDomain() = User(id, email, phone, displayName, isPremium)

class GoalRepositoryImpl(private val db: NutriDatabase) : GoalRepository {
    override val goal: Flow<Goal?> = db.goalDao().observeGoal().map { it?.toDomain() }
    override suspend fun saveGoal(goal: Goal) = withContext(kotlinx.coroutines.Dispatchers.IO) {
        db.goalDao().upsert(GoalEntity(goal.userId, goal.dailyKcal, goal.dailyProtein, goal.dailyFat, goal.dailyCarbs))
    }
}

/**
 * MVP-авторизация: локальный профиль (без бэкенда). Email/телефон валидируется,
 * профиль сохраняется в Room. При появлении сервера — добавим RemoteDataSource
 * с той же сигнатурой, UI не изменится.
 */
class AuthRepositoryImpl(private val db: NutriDatabase) : AuthRepository {

    override val user: Flow<User?> = db.userDao().observeUser().map { it?.toDomain() }

    override suspend fun loginWithEmail(email: String): Result<User> = withContext(kotlinx.coroutines.Dispatchers.IO) {
        val normalized = email.trim()
        when {
            !normalized.matches(Regex("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) ->
                Result.failure(IllegalArgumentException("Введите корректный email"))
            else -> Result.success(saveUser(normalized, null))
        }
    }

    override suspend fun loginWithPhone(phone: String): Result<User> = withContext(kotlinx.coroutines.Dispatchers.IO) {
        val digits = phone.filter { it.isDigit() }
        if (digits.length < 10) Result.failure(IllegalArgumentException("Введите корректный номер телефона"))
        else Result.success(saveUser(null, "+$digits"))
    }

    override suspend fun logout() = withContext(kotlinx.coroutines.Dispatchers.IO) { db.userDao().clear() }

    /** Обновление имени профиля (экран настройки). */
    override suspend fun updateDisplayName(name: String) = withContext(kotlinx.coroutines.Dispatchers.IO) {
        val current = db.userDao().getUser() ?: return@withContext
        db.userDao().upsert(current.copy(displayName = name))
    }

    private suspend fun saveUser(email: String?, phone: String?): User {
        val user = UserEntity(id = 1, email = email, phone = phone, displayName = email ?: phone ?: "", isPremium = false)
        db.userDao().upsert(user)
        return user.toDomain()
    }
}
