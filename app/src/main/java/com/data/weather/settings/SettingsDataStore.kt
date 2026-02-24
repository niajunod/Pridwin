package com.data.weather.settings


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(
    private val context: Context
) {
    companion object {
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
    }

    val notificationsEnabledFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_NOTIFICATIONS] ?: true }

    val darkModeEnabledFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_DARK_MODE] ?: false }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_NOTIFICATIONS] = enabled }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }
}