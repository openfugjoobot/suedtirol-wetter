package com.openfugjoobot.weather.domain.model

import java.time.LocalDateTime

/**
 * Main weather forecast data for a specific station.
 * Contains current conditions and forecast data.
 */
data class WeatherForecast(
    val stationCode: String,
    val stationName: String,
    val coordinates: Coordinates,
    val currentTemperature: Temperature,
    val currentCondition: WeatherCondition,
    val forecast: List<ForecastDay>,
    val lastUpdate: LocalDateTime
)

/**
 * Geographic coordinates for a weather station.
 */
data class Coordinates(
    val latitude: Double,
    val longitude: Double,
    val altitude: Int? = null
) {
    init {
        require(latitude in -90.0..90.0) { "Invalid latitude: $latitude" }
        require(longitude in -180.0..180.0) { "Invalid longitude: $longitude" }
        altitude?.let { require(it in -500..9000) { "Invalid altitude: $it" } }
    }
}
