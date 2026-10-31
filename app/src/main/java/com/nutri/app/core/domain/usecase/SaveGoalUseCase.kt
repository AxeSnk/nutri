package com.nutri.app.core.domain.usecase

import com.nutri.app.core.domain.model.Goal
import com.nutri.app.core.domain.repository.GoalRepository
import com.nutri.app.core.domain.util.MacroCalculator

/** Сохраняет цель пользователя после валидации. Возвращает ошибки или null при успехе. */
class SaveGoalUseCase(private val goalRepository: GoalRepository) {
    suspend operator fun invoke(goal: Goal): List<String> {
        val errors = MacroCalculator.validateGoal(goal.dailyKcal, goal.dailyProtein, goal.dailyFat, goal.dailyCarbs)
        if (errors.isEmpty()) goalRepository.saveGoal(goal)
        return errors
    }
}
