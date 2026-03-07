package com.openfugjoobot.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfugjoobot.weather.data.repository.WeatherRepositoryImpl
import com.openfugjoobot.weather.domain.repository.WeatherRepository
import com.openfugjoobot.weather.presentation.ui.WeatherEvent
import com.openfugjoobot.weather.presentation.ui.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepositoryImpl
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private val stationCode = MutableStateFlow("021029") // Neumarkt Egna
    
    init {
        loadWeather()
    }
    
    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                repository.getWeatherForecast(stationCode.value).collect { forecast ->
                    _uiState.value = WeatherUiState.Success(forecast)
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun onEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.Refresh -> loadWeather()
            is WeatherEvent.StationSelected -> {
                stationCode.value = event.stationCode
                loadWeather()
            }
            is WeatherEvent.ErrorDismissed -> {
                _uiState.value = WeatherUiState.Idle
            }
        }
    }
}
