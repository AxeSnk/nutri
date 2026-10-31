package com.nutri.app

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nutri.app.di.AppGraph
import com.nutri.app.ui.NutriApp
import com.nutri.app.ui.theme.NutriTheme
import java.util.Locale
import kotlinx.coroutines.delay

class MainActivity : androidx.activity.ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val graph = (application as NutriApp).appGraph
        setContent {
            NutriTheme {
                // Спинер при загрузке: пока Room не инициализирован и данные не готовы
                var isAppLoading by remember { mutableStateOf(true) }
                val factory = remember { graph.viewModelFactory() }
                val authVm: com.nutri.app.feature.auth.AuthViewModel = viewModel(factory = factory)
                val loggedIn by authVm.isLoggedIn.collectAsStateWithLifecycle()

                // Русский язык по умолчанию (locale применяется к конфигурации)
                LaunchedEffect(Unit) {
                    applyRussianLocale(this@MainActivity)
                    // Искусственная минимальная задержка для плавного спинера на быстрых устройствах
                    delay(400)
                    isAppLoading = false
                }

                if (isAppLoading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    NutriApp(loggedIn = loggedIn, graph = graph, viewModelFactory = factory)
                }
            }
        }
    }
}

/** Применяет русскую локаль ко всему контексту (строки, формат чисел и дат). */
private fun applyRussianLocale(context: Context) {
    val russian = Locale("ru", "RU")
    Locale.setDefault(russian)
    val config = Configuration(context.resources.configuration)
    config.setLocale(russian)
    context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
