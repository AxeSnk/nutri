package com.nutri.app.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutri.app.core.domain.model.Goal
import com.nutri.app.core.domain.usecase.SaveGoalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalsUiState(
    val kcal: String = "2000",
    val protein: String = "120",
    val fat: String = "65",
    val carbs: String = "220",
    val errors: List<String> = emptyList(),
    val isSaved: Boolean = false,
    val isSaving: Boolean = false
)

class GoalsViewModel(private val saveGoal: SaveGoalUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    fun onFieldChanged(field: Field, value: String) {
        // Пропускаем только цифры — валидация чисел на этапе сохранения
        if (value.isNotEmpty() && !value.all { it.isDigit() }) return
        _uiState.update {
            when (field) {
                Field.KCAL -> it.copy(kcal = value, errors = emptyList())
                Field.PROTEIN -> it.copy(protein = value, errors = emptyList())
                Field.FAT -> it.copy(fat = value, errors = emptyList())
                Field.CARBS -> it.copy(carbs = value, errors = emptyList())
            }
        }
    }

    fun save(userId: Long = 1) {
        val s = _uiState.value
        val goal = Goal(
            userId = userId,
            dailyKcal = s.kcal.toIntOrNull() ?: 0,
            dailyProtein = s.protein.toIntOrNull() ?: 0,
            dailyFat = s.fat.toIntOrNull() ?: 0,
            dailyCarbs = s.carbs.toIntOrNull() ?: 0
        )
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val errors = saveGoal(goal)
            _uiState.update { it.copy(isSaving = false, errors = errors, isSaved = errors.isEmpty()) }
        }
    }
}

enum class Field { KCAL, PROTEIN, FAT, CARBS }
