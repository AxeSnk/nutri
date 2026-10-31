package com.nutri.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutri.app.core.domain.model.Goal
import com.nutri.app.core.domain.model.User
import com.nutri.app.core.domain.repository.AuthRepository
import com.nutri.app.core.domain.repository.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val contact: String? = null,
    val isPremium: Boolean = false,
    val goal: Goal? = null
) {
    val initial: String get() = name.trim().take(1).uppercase().ifBlank { "N" }
}

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(authRepository.user, goalRepository.goal) { user, goal -> user to goal }
                .collect { (user, goal) ->
                    _uiState.update {
                        it.copy(
                            name = user?.displayName ?: "",
                            contact = user?.email ?: user?.phone,
                            isPremium = user?.isPremium ?: false,
                            goal = goal
                        )
                    }
                }
        }
    }

    /**
     * Сохранение имени: обновляет локальный профиль.
     * Текст поля хранится в самом экране (не в StateFlow), иначе IME теряет
     * составляемый ввод (кириллица) из-за асинхронного круга через Flow.
     */
    fun saveName(rawName: String) {
        val newName = rawName.trim()
        if (newName.isBlank()) return
        viewModelScope.launch {
            // AuthRepository хранит одного локального пользователя; обновляем displayName
            authRepository.updateDisplayName(newName)
            _uiState.update { it.copy(name = newName) }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
