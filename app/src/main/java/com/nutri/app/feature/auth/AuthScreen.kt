package com.nutri.app.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Экран входа: email или телефон, локальный профиль. */
@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nutri", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        Text("Калории и БЖУ без сложностей", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        // Переключатель способа входа
        SegmentedButtonRow(mode = state.mode, onModeChanged = viewModel::onModeChanged)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.input,
            onValueChange = viewModel::onInputChanged,
            label = { Text(if (state.mode == AuthMode.EMAIL) "Email" else "Телефон") },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (state.mode == AuthMode.EMAIL) KeyboardType.Email else KeyboardType.Phone
            ),
            isError = state.error != null,
            supportingText = { state.error?.let { Text(it) } },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = viewModel::submit,
            enabled = !state.isLoading && state.input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text("Войти")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentedButtonRow(mode: AuthMode, onModeChanged: (AuthMode) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = mode == AuthMode.EMAIL,
            onClick = { onModeChanged(AuthMode.EMAIL) },
            shape = SegmentedButtonDefaults.itemShape(0, 2)
        ) { Text("По email") }
        SegmentedButton(
            selected = mode == AuthMode.PHONE,
            onClick = { onModeChanged(AuthMode.PHONE) },
            shape = SegmentedButtonDefaults.itemShape(1, 2)
        ) { Text("По телефону") }
    }
}
