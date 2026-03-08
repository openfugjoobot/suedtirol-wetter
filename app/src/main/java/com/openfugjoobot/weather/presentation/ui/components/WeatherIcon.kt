package com.openfugjoobot.weather.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

/**
 * Weather icon URLs from OpenDataHub API
 * Icon codes: 1=Sunny, 2=Partly Cloudy, 3=Cloudy, 4=Rain, 5=Snow, 6=Storm
 */
object WeatherIcons {
    private const val BASE_URL = "https://daten.buergernetz.bz.it/services/weather/graphics/weather_icons/svg/"
    
    fun getIconUrl(iconCode: Int?): String {
        return when (iconCode) {
            1 -> "${BASE_URL}Sonne.svg"
            2 -> "${BASE_URL}Bewoelkt.svg"
            3 -> "${BASE_URL}Bewoelkt.svg"
            4 -> "${BASE_URL}Regen.svg"
            5 -> "${BASE_URL}Schnee.svg"
            6 -> "${BASE_URL}Gewitter.svg"
            else -> "${BASE_URL}Sonne.svg" // Default
        }
    }
}

@Composable
fun WeatherIcon(
    iconCode: Int?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val iconUrl = WeatherIcons.getIconUrl(iconCode)
    
    AsyncImage(
        model = iconUrl,
        contentDescription = contentDescription,
        modifier = modifier
    )
}
