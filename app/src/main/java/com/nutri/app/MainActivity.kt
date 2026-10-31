package com.nutri.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import com.nutri.app.di.AppGraph
import com.nutri.app.ui.NutriApp
import com.nutri.app.ui.theme.NutriTheme

class MainActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val graph = (application as NutriApp).appGraph
        setContent {
            NutriTheme {
                // AuthViewModel определяет: показывать вход или сразу приложение
                val factory = remember { graph.viewModelFactory() }
                val authVm: com.nutri.app.feature.auth.AuthViewModel = viewModel(factory = factory)
                val loggedIn by authVm.isLoggedIn.collectAsStateWithLifecycle()
                NutriApp(loggedIn = loggedIn, graph = graph, viewModelFactory = factory)
            }
        }
    }
}
