package com.openfugjoobot.weather.domain.model

/**
 * Temperature data with min, max, and current values.
 * All temperatures in Celsius.
 */
data class Temperature(
    val current: Double? = null,
    val minimum: Double? = null,
    val maximum: Double? = null,
    val feelsLike: Double? = null
)
