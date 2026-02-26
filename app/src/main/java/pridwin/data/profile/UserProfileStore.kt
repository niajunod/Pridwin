//Nia Junod & Alina Tarasevich
package pridwin.data.profile

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pridwin.domain.model.CommuteMode
import pridwin.domain.model.Role

private val Context.profileDataStore by preferencesDataStore(name = "user_profile")

class UserProfileStore(private val context: Context) {

    private object Keys {
        val ROLE = stringPreferencesKey("role")
        val COMMUTE_MODE = stringPreferencesKey("commute_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LAST_LAT = doublePreferencesKey("last_lat")
        val LAST_LON = doublePreferencesKey("last_lon")
    }

    val roleFlow: Flow<Role?> =
        context.profileDataStore.data.map { prefs ->
            prefs[Keys.ROLE]?.let { saved ->
                runCatching { Role.valueOf(saved) }.getOrNull()
            }
        }

    val commuteModeFlow: Flow<CommuteMode> =
        context.profileDataStore.data.map { prefs ->
            val saved = prefs[Keys.COMMUTE_MODE]
            runCatching {
                if (saved != null) CommuteMode.valueOf(saved)
                else CommuteMode.WALK_FROM_PARKING
            }.getOrDefault(CommuteMode.WALK_FROM_PARKING)
        }

    val notificationsEnabledFlow: Flow<Boolean> =
        context.profileDataStore.data.map { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setRole(role: Role?) {
        context.profileDataStore.edit { prefs ->
            if (role == null) prefs.remove(Keys.ROLE)
            else prefs[Keys.ROLE] = role.name
        }
    }

    suspend fun setCommuteMode(mode: CommuteMode) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.COMMUTE_MODE] = mode.name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setLastLocation(lat: Double, lon: Double) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.LAST_LAT] = lat
            prefs[Keys.LAST_LON] = lon
        }
    }

    // Clean worker-friendly reads
    suspend fun getRoleOnce(defaultRole: Role = Role.MAIN_BARTENDER): Role {
        return roleFlow.first() ?: defaultRole
    }

    suspend fun getNotificationsEnabledOnce(default: Boolean = true): Boolean {
        return notificationsEnabledFlow.first() ?: default
    }
}