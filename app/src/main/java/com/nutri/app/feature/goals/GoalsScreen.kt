package com.nutri.app.feature.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Экран установки суточной цели калорий и БЖУ. */
@Composable
fun GoalsScreen(viewModel: GoalsViewModel, onSaved: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Ваша цель", style = MaterialTheme.typography.headlineMedium)
        Text("Суточная норма калорий и БЖУ", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        NumberField("Калорий в день, ккал", state.kcal) { viewModel.onFieldChanged(Field.KCAL, it) }
        Spacer(Modifier.height(12.dp))
        NumberField("Белки, г", state.protein) { viewModel.onFieldChanged(Field.PROTEIN, it) }
        Spacer(Modifier.height(12.dp))
        NumberField("Жиры, г", state.fat) { viewModel.onFieldChanged(Field.FAT, it) }
        Spacer(Modifier.height(12.dp))
        NumberField("Углеводы, г", state.carbs) { viewModel.onFieldChanged(Field.CARBS, it) }

        // Валидационные ошибки из домена (чистая логика MacroCalculator)
        state.errors.forEach { error ->
            AnimatedVisibility(true) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = viewModel::save, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) {
            Text("Сохранить цель")
        }
    }
}

@Composable
private fun NumberField(label: String, value: String, onChanged: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChanged,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
