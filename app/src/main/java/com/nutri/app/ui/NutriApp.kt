package com.nutri.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nutri.app.ui.theme.NutriColors
import kotlinx.coroutines.flow.map
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nutri.app.di.AppGraph
import com.nutri.app.feature.auth.AuthScreen
import com.nutri.app.feature.builder.BuilderScreen
import com.nutri.app.feature.diary.DiaryScreen
import com.nutri.app.feature.goals.GoalsScreen
import com.nutri.app.feature.home.HomeScreen

/** Маршруты. Home — dashboard по макету, builder/diary — рабочие табы. */
private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    Tab("home", "Главная", Icons.Default.Home),
    Tab("builder", "Конструктор", Icons.Default.Restaurant),
    Tab("diary", "Дневник", Icons.Default.MenuBook)
)

/**
 * Корневая навигация: Вход → Цели → табы (Home / Конструктор / Дневник).
 * Телефон — нижняя панель, планшет (ширина ≥ 840dp) — NavigationRail слева, как в макете.
 */
@Composable
fun NutriApp(loggedIn: Boolean, graph: AppGraph, viewModelFactory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showTabs = tabs.any { it.route == currentRoute }
    val wide = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 840

    // null = цель ещё читается из БД; true/false = цель установлена / не установлена.
    val hasGoal by remember { graph.goalRepository.goal.map { it != null } }
        .collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(loggedIn, currentRoute, hasGoal) {
        if (loggedIn && currentRoute == "auth") {
            val goalSet = hasGoal ?: return@LaunchedEffect // ждём ответ из БД
            // Экран цели — только если цель ещё не задана
            navController.navigate(if (goalSet) "home" else "goals") {
                popUpTo("auth") { inclusive = true }
            }
        } else if (!loggedIn && currentRoute != null && currentRoute != "auth") {
            navController.navigate("auth") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val navBody: @Composable (Modifier) -> Unit = { padding ->
        NavHost(
            navController = navController,
            startDestination = if (loggedIn) "home" else "auth",
            modifier = padding,
            enterTransition = { fadeIn() + slideInHorizontally { it / 6 } },
            exitTransition = { fadeOut() }
        ) {
            composable("auth") {
                val vm: com.nutri.app.feature.auth.AuthViewModel = viewModel(factory = viewModelFactory)
                AuthScreen(vm)
            }
            composable("goals") {
                val vm: com.nutri.app.feature.goals.GoalsViewModel = viewModel(factory = viewModelFactory)
                GoalsScreen(vm, onSaved = {
                    navController.navigate("home") { popUpTo("goals") { inclusive = true } }
                })
            }
            composable("home") {
                val vm: com.nutri.app.feature.home.HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(vm, onAvatarClick = { navController.navigate("profile") })
            }
            composable("profile") {
                val vm: com.nutri.app.feature.profile.ProfileViewModel = viewModel(factory = viewModelFactory)
                com.nutri.app.feature.profile.ProfileScreen(vm, onBack = { navController.popBackStack() })
            }
            composable("builder") {
                val vm: com.nutri.app.feature.builder.BuilderViewModel = viewModel(factory = viewModelFactory)
                BuilderScreen(vm)
            }
            composable("diary") {
                val vm: com.nutri.app.feature.diary.DiaryViewModel = viewModel(factory = viewModelFactory)
                DiaryScreen(vm)
            }
        }
    }

    if (!showTabs) {
        navBody(Modifier) // экраны входа/целей без навигации
    } else if (wide) {
        // Планшет: боковой rail + контент (как левая колонка меню на макете)
        Row {
            NavigationRail(containerColor = NutriColors.Card) {
                tabs.forEach { tab ->
                    NavigationRailItem(
                        selected = currentRoute == tab.route,
                        onClick = { navController.navigate(tab.route) { launchSingleTop = true } },
                        icon = { Icon(tab.icon, null) },
                        label = { Text(tab.label) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = NutriColors.Red,
                            selectedTextColor = NutriColors.Red,
                            unselectedIconColor = NutriColors.Icon,
                            unselectedTextColor = NutriColors.Inactive,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
            navBody(Modifier.weight(1f))
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = NutriColors.Card) {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = { navController.navigate(tab.route) { launchSingleTop = true } },
                            icon = { Icon(tab.icon, null) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NutriColors.Red,
                                selectedTextColor = NutriColors.Red,
                                unselectedIconColor = NutriColors.Icon,
                                unselectedTextColor = NutriColors.Inactive,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { padding -> navBody(Modifier.padding(padding)) }
    }
}
