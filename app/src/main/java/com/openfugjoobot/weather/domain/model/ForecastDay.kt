package com.openfugjoobot.weather.domain.model

import java.time.LocalDate

/**
 * Daily weather forecast entry.
 */
data class ForecastDay(
    val date: LocalDate,
    val temperature: Temperature,
    val condition: WeatherCondition,
    val precipitationProbability: Int? = null,
    val humidity: Int? = null,
    val windSpeed: Double? = null
)
