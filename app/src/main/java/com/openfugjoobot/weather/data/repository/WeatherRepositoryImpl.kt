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
    private val cacheValidityMinutes = com.openfugjoobot.weather.util.Config.CACHE_VALIDITY_MINUTES
    
    override fun getWeatherForecast(stationCode: String): Flow<WeatherForecast> = flow {
        var retryCount = 0
        val maxRetries = 3
        
        while (retryCount < maxRetries) {
            try {
                // Check cache
                val now = LocalDateTime.now()
                val cacheValid = lastFetched?.let {
                    java.time.Duration.between(it, now).toMinutes() < cacheValidityMinutes
                } ?: false
                
                if (cacheValid) {
                    cachedForecast?.let { emit(it) }
                    return@flow
                }
                
                // Fetch from API
                val response = ApiClient.weatherApiService.getWeatherForecast(stationCode)
                
                val apiData = response.body()
                if (response.isSuccessful && apiData != null) {
                    // Convert to domain model
                    val forecast = convertToDomain(apiData, stationCode)
                    cachedForecast = forecast
                    lastFetched = now
                    
                    emit(forecast)
                    return@flow
                } else {
                    // API error - try cache fallback
                    cachedForecast?.let { emit(it) } ?: throw Exception("API error: ${response.code()}")
                }
            } catch (e: Exception) {
                retryCount++
                if (retryCount >= maxRetries) {
                    // All retries failed - try cache fallback
                    cachedForecast?.let { emit(it) } ?: throw e
                }
                // Wait before retry (exponential backoff: 1s, 2s, 4s)
                kotlinx.coroutines.delay(1000L * (1L shl retryCount))
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
            forecast = apiData.data.map { apiEntry ->
                ForecastDay(
                    date = Instant.ofEpochSecond(apiEntry.epoch).atZone(ZoneId.systemDefault()).toLocalDate(),
                    temperature = Temperature(
                        current = apiEntry.temperature?.value,
                        minimum = apiEntry.temperature?.value, // API may not provide min/max
                        maximum = apiEntry.temperature?.value
                    ),
                    condition = WeatherCondition(
                        description = apiEntry.weatherCondition ?: "Unknown",
                        iconCode = apiEntry.weatherConditionType ?: "unknown",
                        type = mapToConditionType(apiEntry.weatherConditionType)
                    ),
                    humidity = null, // Not provided by API
                    windSpeed = null,
                    precipitationProbability = null
                )
            },
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
