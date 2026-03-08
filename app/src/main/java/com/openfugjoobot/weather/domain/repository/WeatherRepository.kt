package com.openfugjoobot.weather.domain.repository

import com.openfugjoobot.weather.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for weather data
 */
interface WeatherRepository {
    
    /**
     * Get weather forecast for a station as a reactive stream
     * @param stationCode Station code (e.g., "021029")
     */
    fun getWeatherForecast(stationCode: String): Flow<WeatherForecast>
}
