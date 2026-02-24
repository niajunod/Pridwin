package com.example.pridwin.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pridwin.domain.model.ContextState
import com.example.pridwin.domain.model.Recommendation
import com.example.pridwin.domain.model.WeatherInfo
import com.example.pridwin.domain.usecase.GetWeatherAndRecommendationsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val weather: WeatherInfo? = null,
    val context: ContextState? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val getWeatherAndRecommendationsUseCase: GetWeatherAndRecommendationsUseCase =
        GetWeatherAndRecommendationsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            runCatching {
                getWeatherAndRecommendationsUseCase()
            }.onSuccess { result ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        weather = result.weather,
                        context = result.context,
                        recommendations = result.recommendations
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(isLoading = false, error = it.error ?: "Unknown error")
                }
            }
        }
    }
}