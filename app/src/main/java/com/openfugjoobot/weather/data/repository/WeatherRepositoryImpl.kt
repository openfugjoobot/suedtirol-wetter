package com.openfugjoobot.weather.data.repository

import com.openfugjoobot.weather.data.api.ApiClient
import com.openfugjoobot.weather.data.api.ApiWeatherForecast
import com.openfugjoobot.weather.domain.model.*
import com.openfugjoobot.weather.domain.repository.WeatherRepository
import com.openfugjoobot.weather.util.Config
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Repository implementation with caching
 */
class WeatherRepositoryImpl @Inject constructor() : WeatherRepository {
    
    private var lastFetched: LocalDateTime? = null
    private var cachedForecast: WeatherForecast? = null
    
    override fun getWeatherForecast(stationCode: String): Flow<WeatherForecast> = flow {
        var retryCount = 0
        val maxRetries = Config.MAX_NETWORK_RETRIES
        
        // FORCE CACHE CLEAR on EVERY call (DEBUG/DEV MODE)
        cachedForecast = null
        lastFetched = null
        android.util.Log.d("WEATHER_API", "⚠️ CACHE KILLED - fetching FRESH data from API!")
        android.util.Log.d("WEATHER_API", "Requested station: $stationCode")
        
        while (retryCount < maxRetries) {
            try {
                // SKIP cache check in debug mode - always fetch
                // Check cache (disabled for debugging)
                /*
                val now = LocalDateTime.now()
                val cacheValid = lastFetched?.let {
                    java.time.Duration.between(it, now).toMinutes() < Config.CACHE_VALIDITY_MINUTES
                } ?: false
                
                if (cacheValid && cachedForecast != null) {
                    emit(cachedForecast!!)
                    return@flow
                }
                */
                
                // Fetch from API (returns ALL forecasts, filter by station code)
                val response = ApiClient.weatherApiService.getWeatherForecast()
                
                val allForecasts = response.body()
                if (response.isSuccessful && allForecasts != null) {
                    // DEBUG: Log all available stations
                    android.util.Log.d("WEATHER_API", "Received ${allForecasts.size} forecasts")
                    if (android.util.Log.isLoggable("WEATHER_API", android.util.Log.DEBUG)) {
                        allForecasts.forEach { f ->
                            android.util.Log.d("WEATHER_API", "  - ${f.Shortname} (${f.MunicipalityIstatCode}): ${f.ForeCastDaily.firstOrNull()?.MaxTemp}°C")
                        }
                    }
                    
                    // Filter by station code
                    val stationForecast = allForecasts.find { 
                        it.MunicipalityIstatCode == stationCode 
                    }
                    
                    // Fallback to first item if not found (for debugging)
                    val finalForecast = stationForecast ?: run {
                        android.util.Log.e("WEATHER_API", "Station $stationCode not found! Using first result: ${allForecasts.firstOrNull()?.Shortname}")
                        allForecasts.firstOrNull()
                    }
                    
                    if (finalForecast != null) {
                        // Convert to domain model
                        val forecast = convertToDomain(finalForecast)
                        cachedForecast = forecast
                        lastFetched = now
                        
                        emit(forecast)
                        return@flow
                    } else {
                        throw Exception("No data found for station: $stationCode")
                    }
                } else {
                    // API error - try cache fallback
                    cachedForecast?.let { emit(it) } 
                        ?: throw Exception("API error: ${response.code()}")
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
    
    private fun convertToDomain(apiData: ApiWeatherForecast): WeatherForecast {
        // Get coordinates from GpsInfo or use defaults
        val lat = apiData.GpsInfo?.Latitude ?: 0.0
        val lon = apiData.GpsInfo?.Longitude ?: 0.0
        val alt = apiData.GpsInfo?.Altitude ?: 0
        
        // Get current day's forecast (first entry)
        val todayForecast = apiData.ForeCastDaily.firstOrNull() 
            ?: throw Exception("No forecast data available")
        
        // Map weather code to condition type
        val conditionType = mapWeatherCode(todayForecast.WeatherCode)
        
        // Build domain model
        return WeatherForecast(
            stationCode = apiData.MunicipalityIstatCode,
            stationName = apiData.Shortname,
            coordinates = Coordinates(
                latitude = lat,
                longitude = lon,
                altitude = alt
            ),
            currentTemperature = Temperature(
                current = todayForecast.MinTemp.toDouble(), // Use min as "current" approximation
                minimum = todayForecast.MinTemp.toDouble(),
                maximum = todayForecast.MaxTemp.toDouble()
            ),
            currentCondition = WeatherCondition(
                description = todayForecast.WeatherDesc,
                iconCode = todayForecast.WeatherCode,
                type = conditionType
            ),
            forecast = apiData.ForeCastDaily.map { daily ->
                ForecastDay(
                    date = java.time.LocalDate.parse(daily.Date.split('T').first()),
                    temperature = Temperature(
                        minimum = daily.MinTemp.toDouble(),
                        maximum = daily.MaxTemp.toDouble()
                    ),
                    condition = WeatherCondition(
                        description = daily.WeatherDesc,
                        iconCode = daily.WeatherCode,
                        type = mapWeatherCode(daily.WeatherCode)
                    ),
                    humidity = null,
                    windSpeed = null,
                    precipitationProbability = daily.SunshineDuration
                )
            },
            lastUpdate = LocalDateTime.now()
        )
    }
    
    private fun mapWeatherCode(code: String?): ConditionType {
        return when (code?.lowercase()) {
            "a", "clear", "sunny" -> ConditionType.SUNNY
            "b", "partly_cloudy" -> ConditionType.PARTLY_CLOUDY
            "c", "d", "cloudy", "overcast" -> ConditionType.CLOUDY
            "g", "h", "rain", "rainy", "drizzle" -> ConditionType.RAIN
            "i", "snow" -> ConditionType.SNOW
            "k", "t", "storm", "thunderstorm" -> ConditionType.STORM
            "f", "fog", "mist" -> ConditionType.FOG
            else -> ConditionType.UNKNOWN
        }
    }
}
