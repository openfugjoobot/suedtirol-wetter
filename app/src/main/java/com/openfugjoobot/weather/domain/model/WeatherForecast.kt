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
)
