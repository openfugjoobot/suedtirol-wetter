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
) {
    init {
        // Validate realistic temperature range (-100 to 60 celsius)
        current?.let { require(it in -100.0..60.0) { "Invalid temperature: $it" } }
        minimum?.let { require(it in -100.0..60.0) { "Invalid minimum temperature: $it" } }
        maximum?.let { require(it in -100.0..60.0) { "Invalid maximum temperature: $it" } }
        feelsLike?.let { require(it in -100.0..60.0) { "Invalid feels like temperature: $it" } }
    }
}
