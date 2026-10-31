package com.nutri.app.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.nutri.app.core.data.local.NutriDatabase
import com.nutri.app.core.data.remote.ApiFactory
import com.nutri.app.core.data.remote.OpenFoodFactsApi
import com.nutri.app.core.data.repository.AuthRepositoryImpl
import com.nutri.app.core.data.repository.DiaryRepositoryImpl
import com.nutri.app.core.data.repository.DishRepositoryImpl
import com.nutri.app.core.data.repository.GoalRepositoryImpl
import com.nutri.app.core.domain.ai.AiRecommender
import com.nutri.app.core.domain.ai.NoAiRecommender
import com.nutri.app.core.domain.repository.AuthRepository
import com.nutri.app.core.domain.repository.DiaryRepository
import com.nutri.app.core.domain.repository.DishRepository
import com.nutri.app.core.domain.repository.GoalRepository
import com.nutri.app.core.domain.usecase.GetRecommendationsUseCase
import com.nutri.app.core.domain.usecase.LogDishUseCase
import com.nutri.app.core.domain.usecase.ObserveDailyProgressUseCase
import com.nutri.app.core.domain.usecase.SaveGoalUseCase
import androidx.lifecycle.ViewModelProvider

/**
 * Явный граф зависимостей (без DI-фреймворка — MVP).
 * Платный AI-модуль при подключении заменит NoAiRecommender на свою реализацию.
 */
class AppGraph(context: Context) {

    val database: NutriDatabase = NutriDatabase.build(context)
    val api: OpenFoodFactsApi = ApiFactory.create(context.cacheDir) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let(connectivityManager::getNetworkCapabilities)
        capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    val aiRecommender: AiRecommender = NoAiRecommender

    // Repositories
    val dishRepository: DishRepository = DishRepositoryImpl(database, api)
    val diaryRepository: DiaryRepository = DiaryRepositoryImpl(database)
    val goalRepository: GoalRepository = GoalRepositoryImpl(database)
    val authRepository: AuthRepository = AuthRepositoryImpl(database)

    // UseCases
    val getRecommendations: GetRecommendationsUseCase = GetRecommendationsUseCase(dishRepository)
    val observeDailyProgress: ObserveDailyProgressUseCase = ObserveDailyProgressUseCase(diaryRepository, goalRepository)
    val saveGoal: SaveGoalUseCase = SaveGoalUseCase(goalRepository)
    val logDish: LogDishUseCase = LogDishUseCase(diaryRepository)

    /** Фабрика для viewModel() в Compose. */
    fun viewModelFactory(): ViewModelProvider.Factory = com.nutri.app.di.NutriViewModelFactory(this)
}

/** Простая фабрика ViewModel, создающая их через AppGraph. */
class NutriViewModelFactory(private val graph: AppGraph) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        com.nutri.app.feature.auth.AuthViewModel::class.java ->
            com.nutri.app.feature.auth.AuthViewModel(graph.authRepository) as T
        com.nutri.app.feature.builder.BuilderViewModel::class.java ->
            com.nutri.app.feature.builder.BuilderViewModel(graph.dishRepository, graph.getRecommendations, graph.logDish, graph.aiRecommender) as T
        com.nutri.app.feature.goals.GoalsViewModel::class.java ->
            com.nutri.app.feature.goals.GoalsViewModel(graph.saveGoal) as T
        com.nutri.app.feature.diary.DiaryViewModel::class.java ->
            com.nutri.app.feature.diary.DiaryViewModel(graph.observeDailyProgress) as T
        com.nutri.app.feature.home.HomeViewModel::class.java ->
            com.nutri.app.feature.home.HomeViewModel(graph.dishRepository, graph.goalRepository, graph.diaryRepository, graph.authRepository, graph.getRecommendations) as T
        com.nutri.app.feature.profile.ProfileViewModel::class.java ->
            com.nutri.app.feature.profile.ProfileViewModel(graph.authRepository, graph.goalRepository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
    }
}
