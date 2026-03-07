package com.openfugjoobot.weather.presentation.ui

import com.openfugjoobot.weather.domain.model.WeatherForecast

/**
 * UI state for WeatherScreen
 */
sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val forecast: WeatherForecast) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

/**
 * UI events from user interactions
 */
sealed class WeatherEvent {
    object Refresh : WeatherEvent()
    data class StationSelected(val stationCode: String) : WeatherEvent()
    object ErrorDismissed : WeatherEvent()
}
