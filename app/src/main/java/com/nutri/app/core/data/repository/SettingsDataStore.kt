package com.nutri.app.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "nutri_settings")

/** Настройки UI: тема, heuristics onboarding. Пользовательские данные — в Room. */
class SettingsDataStore(private val context: Context) {

    private val THEME_KEY = stringPreferencesKey("theme")
    private val ONBOARDED_KEY = booleanPreferencesKey("onboarded")

    val theme: Flow<String> = context.dataStore.data.map { it[THEME_KEY] ?: "system" }
    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDED_KEY] ?: false }

    suspend fun setTheme(value: String) = context.dataStore.edit { it[THEME_KEY] = value }
    suspend fun setOnboarded() = context.dataStore.edit { it[ONBOARDED_KEY] = true }
}
