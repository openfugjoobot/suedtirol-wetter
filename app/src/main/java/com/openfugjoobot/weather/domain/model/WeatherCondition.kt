package com.openfugjoobot.weather.domain.model

/**
 * Qualitative weather condition (e.g., "partly cloudy", "sunny").
 * Includes icon representation for UI.
 */
data class WeatherCondition(
    val description: String,
    val iconCode: String,
    val type: ConditionType
)

enum class ConditionType(val displayName: String) {
    SUNNY("Sunny"),
    PARTLY_CLOUDY("Partly Cloudy"),
    CLOUDY("Cloudy"),
    OVERCAST("Overcast"),
    RAIN("Rain"),
    SNOW("Snow"),
    STORM("Storm"),
    FOG("Fog"),
    UNKNOWN("Unknown")
}
