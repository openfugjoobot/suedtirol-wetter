package com.openfugjoobot.weather.data.repository

import com.openfugjoobot.weather.data.api.ApiClient
import com.openfugjoobot.weather.domain.model.*
import com.openfugjoobot.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Repository implementation with caching
 */
class WeatherRepositoryImpl : WeatherRepository {
    
    private var lastFetched: LocalDateTime? = null
    private var cachedForecast: WeatherForecast? = null
    private val cacheValidityMinutes = 5
    
    override fun getWeatherForecast(stationCode: String): Flow<WeatherForecast> = flow {
        
        // Check cache
        val now = LocalDateTime.now()
        val cacheValid = lastFetched?.let {
            java.time.Duration.between(it, now).toMinutes() < cacheValidityMinutes
        } ?: false
        
        if (cacheValid && cachedForecast != null) {
            emit(cachedForecast!!)
            return@flow
        }
        
        try {
            // Fetch from API
            val response = ApiClient.weatherApiService.getWeatherForecast(stationCode)
            
            if (response.isSuccessful && response.body() != null) {
                val apiData = response.body()!!
                
                // Convert to domain model
                val forecast = convertToDomain(apiData, stationCode)
                cachedForecast = forecast
                lastFetched = now
                
                emit(forecast)
            } else {
                // API error - try cache fallback
                if (cachedForecast != null) {
                    emit(cachedForecast!!)
                } else {
                    throw Exception("API error: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            // Network error - try cache fallback
            if (cachedForecast != null) {
                emit(cachedForecast!!)
            } else {
                throw e
            }
        }
    }
    
    private fun convertToDomain(apiData: com.openfugjoobot.weather.data.api.WeatherApiResponse, stationCode: String): WeatherForecast {
        val data = apiData.data.firstOrNull() ?: throw Exception("No data")
        
        return WeatherForecast(
            stationCode = stationCode,
            stationName = data.sName,
            coordinates = Coordinates(
                latitude = data.latitude,
                longitude = data.longitude,
                altitude = data.altitude
            ),
            currentTemperature = Temperature(
                current = data.temperature?.value
            ),
            currentCondition = WeatherCondition(
                description = data.weatherCondition ?: "Unknown",
                iconCode = data.weatherConditionType ?: "unknown",
                type = mapToConditionType(data.weatherConditionType)
            ),
            forecast = emptyList(), // Simplified - would map apiData.data to ForecastDay list
            lastUpdate = LocalDateTime.now()
        )
    }
    
    private fun mapToConditionType(type: String?): ConditionType {
        return when (type?.lowercase()) {
            "clear", "sunny" -> ConditionType.SUNNY
            "partly_cloudy", "partly cloudy" -> ConditionType.PARTLY_CLOUDY
            "cloudy", "overcast" -> ConditionType.CLOUDY
            "rain", "rainy" -> ConditionType.RAIN
            "snow" -> ConditionType.SNOW
            "storm", "thunderstorm" -> ConditionType.STORM
            "fog", "mist" -> ConditionType.FOG
            else -> ConditionType.UNKNOWN
        }
    }
}
