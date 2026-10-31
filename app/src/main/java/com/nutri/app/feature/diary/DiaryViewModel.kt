package com.nutri.app.feature.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutri.app.core.domain.usecase.ObserveDailyProgressUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/**
 * Дневник: подписка на агрегат (записи + цель + история) через UseCase.
 * stateIn — Flow живёт пока жив экран и стартует сразу.
 */
class DiaryViewModel(
    observeDailyProgress: ObserveDailyProgressUseCase,
    userId: Long = 1
) : ViewModel() {

    val progress: StateFlow<ObserveDailyProgressUseCase.Progress?> =
        observeDailyProgress(userId, LocalDate.now().toString())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
