package com.nutri.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutri.app.core.domain.model.User
import com.nutri.app.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Состояние экрана входа: поле, ошибки, загрузка. */
data class AuthUiState(
    val mode: AuthMode = AuthMode.EMAIL,
    val input: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class AuthMode { EMAIL, PHONE }

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    /** Пользователь null = не авторизован. StateFlow — единый источник истины. */
    val isLoggedIn: StateFlow<Boolean> = authRepository.user
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onInputChanged(value: String) {
        _uiState.update { it.copy(input = value, error = null) }
    }

    fun onModeChanged(mode: AuthMode) {
        _uiState.update { it.copy(mode = mode, input = "", error = null) }
    }

    fun submit() {
        val state = _uiState.value
        if (state.input.isBlank()) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val result = when (state.mode) {
                AuthMode.EMAIL -> authRepository.loginWithEmail(state.input)
                AuthMode.PHONE -> authRepository.loginWithPhone(state.input)
            }
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false) } },
                onFailure = { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }
}
