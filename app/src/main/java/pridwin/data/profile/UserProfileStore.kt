package pridwin.data.profile

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pridwin.domain.model.CommuteMode
import pridwin.domain.model.Role


class UserProfileStore(context: Context) {

    private val _roleFlow = MutableStateFlow<Role?>(null)
    val roleFlow: StateFlow<Role?> = _roleFlow

    private val _commuteModeFlow = MutableStateFlow(CommuteMode.WALK_FROM_PARKING)
    val commuteModeFlow: StateFlow<CommuteMode> = _commuteModeFlow

    private val _notificationsEnabledFlow = MutableStateFlow(true)
    val notificationsEnabledFlow: StateFlow<Boolean> = _notificationsEnabledFlow

    fun setRole(role: Role?) {
        _roleFlow.value = role
    }

    fun setCommuteMode(mode: CommuteMode) {
        _commuteModeFlow.value = mode
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabledFlow.value = enabled
    }
}