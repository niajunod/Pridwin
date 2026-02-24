package pridwin.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DebugUiState(
    val logs: List<String> = emptyList()
)

class DebugViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DebugUiState())
    val uiState: StateFlow<DebugUiState> = _uiState.asStateFlow()

    fun addLog(message: String) {
        _uiState.value =
            _uiState.value.copy(logs = _uiState.value.logs + message)
    }

    fun clearLogs() {
        _uiState.value = DebugUiState()
    }
}