package com.nutri.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Экран настройки профиля: имя, email/телефон, цель, премиум-статус, выход. */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // Локальное состояние поля: ввод применяется синхронно, кириллица не теряется
    var nameInput by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Назад") }
        }
        Spacer(Modifier.height(8.dp))

        // Аватар + имя
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.initial,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(state.name.ifBlank { "Пользователь" },
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            state.contact?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(20.dp))

        // Редактирование имени
        ProfilePanel("Личные данные") {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Имя") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.saveName(nameInput)
                    nameInput = ""
                },
                enabled = nameInput.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
        Spacer(Modifier.height(16.dp))

        // Цель
        ProfilePanel("Моя цель") {
            state.goal?.let { g ->
                Text("${g.dailyKcal} ккал в день", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Б ${g.dailyProtein} г · Ж ${g.dailyFat} г · У ${g.dailyCarbs} г",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } ?: Text("Цель не установлена", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(16.dp))

        // Premium
        ProfilePanel("Подписка") {
            Text(
                if (state.isPremium) "Premium активна ✨" else "Бесплатный план",
                style = MaterialTheme.typography.titleMedium
            )
            if (!state.isPremium) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "ИИ-рекомендации рациона — в Premium",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Button(onClick = { /* upsell: платный AI-модуль */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Оформить Premium")
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = viewModel::logout,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Выйти") }
    }
}

@Composable
private fun ProfilePanel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
