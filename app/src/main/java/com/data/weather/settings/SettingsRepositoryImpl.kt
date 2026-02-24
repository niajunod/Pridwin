package com.data.weather.settings

import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val notificationsEnabled: Flow<Boolean> = dataStore.notificationsEnabledFlow
    override val darkModeEnabled: Flow<Boolean> = dataStore.darkModeEnabledFlow

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.setNotificationsEnabled(enabled)
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.setDarkModeEnabled(enabled)
    }
}