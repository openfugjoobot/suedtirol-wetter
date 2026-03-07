package com.openfugjoobot.weather.domain.model

/**
 * Qualitative weather condition (e.g., "partly cloudy", "sunny").
 * Includes icon representation for UI.
 */
data class WeatherCondition(
    val description: String?,  // Can be null if API doesn't provide it
    val iconCode: String?,     // Can be null
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
