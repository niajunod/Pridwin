package pridwin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pridwin.data.profile.UserProfileStore
import pridwin.data.weather.WeatherRepository
import pridwin.domain.model.CommuteMode
import pridwin.domain.model.DayShiftStatus
import pridwin.domain.model.ForecastDay
import pridwin.domain.model.Role
import pridwin.domain.model.ShiftBrief
import pridwin.domain.model.ShiftInstance
import pridwin.domain.rules.ShiftBriefEngine
import pridwin.domain.usecase.GetNextShiftInstanceUseCase
import pridwin.domain.usecase.GiveFiveDayShiftScheduleUseCase
import java.time.LocalDateTime

data class ScheduleUiState(
    val role: Role? = null,
    val commuteMode: CommuteMode = CommuteMode.WALK_FROM_PARKING,
    val notificationsEnabled: Boolean = true,

    val isLoading: Boolean = false,
    val error: String? = null,

    val nextShift: ShiftInstance? = null,
    val shiftBrief: ShiftBrief? = null,
    val fiveDay: List<DayShiftStatus> = emptyList()
)

class ScheduleViewModel(
    private val profileStore: UserProfileStore,
    private val weatherRepo: WeatherRepository,
    private val getNextShift: GetNextShiftInstanceUseCase = GetNextShiftInstanceUseCase(),
    private val giveFiveDay: GiveFiveDayShiftScheduleUseCase = GiveFiveDayShiftScheduleUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState(isLoading = true))
    val uiState: StateFlow<ScheduleUiState> = _uiState

    fun refresh(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val role = profileStore.roleFlow.first()
                val commute = profileStore.commuteModeFlow.first()
                val notif = profileStore.notificationsEnabledFlow.first()

                if (role == null) {
                    _uiState.value = ScheduleUiState(
                        role = null,
                        commuteMode = commute,
                        notificationsEnabled = notif,
                        isLoading = false,
                        error = "Select a role in Settings."
                    )
                    return@launch
                }

                val forecastDays: List<ForecastDay> =
                    weatherRepo.getFiveDayForecast(lat, lon)
                        .sortedBy { it.date }
                        .take(5)

                val now = LocalDateTime.now()
                val nextShiftInstance = getNextShift.execute(role, now)

                val slicesForShiftDay = forecastDays
                    .firstOrNull { it.date == nextShiftInstance.date }
                    ?.slices
                    ?: forecastDays.flatMap { it.slices }

                val brief = ShiftBriefEngine.generate(
                    role = role,
                    shift = nextShiftInstance,
                    allSlices = slicesForShiftDay,
                    now = now,
                    commuteMode = commute
                )

                val schedule = giveFiveDay.execute(role, forecastDays)

                _uiState.value = ScheduleUiState(
                    role = role,
                    commuteMode = commute,
                    notificationsEnabled = notif,
                    isLoading = false,
                    error = null,
                    nextShift = nextShiftInstance,
                    shiftBrief = brief,
                    fiveDay = schedule
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = t.message ?: "Unknown error"
                )
            }
        }
    }
}

class ScheduleViewModelFactory(
    private val profileStore: UserProfileStore,
    private val weatherRepo: WeatherRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ScheduleViewModel::class.java))
        return ScheduleViewModel(profileStore, weatherRepo) as T
    }
}