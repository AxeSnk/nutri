package com.nutri.app.core.domain.ai

import com.nutri.app.core.domain.model.Dish
import com.nutri.app.core.domain.model.Goal

/**
 * Контракт платного AI-модула. MVP ничего о нём не знает — модуль реализует
 * этот интерфейс и регистрируется в AppGraph при подключении (`:ai`).
 * Здесь только интерфейс — зависимости к платным SDK живут в отдельном модуле.
 */
interface AiRecommender {
    /** Возвращает персональные рекомендации блюд под цель и историю пользователя. */
    suspend fun recommend(goal: Goal, recentDishIds: List<String>, limit: Int): List<Dish>

    /** Доступен ли AI (подписка активна). */
    val isAvailable: Boolean
}

/** Заглушка для MVP: AI недоступен, UI показывает upsell-баннер. */
object NoAiRecommender : AiRecommender {
    override val isAvailable = false
    override suspend fun recommend(goal: Goal, recentDishIds: List<String>, limit: Int) = emptyList<Dish>()
}
