package com.openfugjoobot.weather.data.api

/**
 * API constants for OpenDataHub Tourism API v1
 * 
 * Base URL: https://tourism.api.opendatahub.com
 * Documentation: https://opendatahub.com/develop/apis-and-tools/api#/Tourism
 */
object ApiConstants {
    const val BASE_URL = "https://tourism.api.opendatahub.com"
    const val ENDPOINT_WEATHER_FORECAST = "/v1/Weather/Forecast"
    
    // Neumarkt-Egna ISTAT code
    const val DEFAULT_STATION_CODE = "021059"
    
    // Rate limiting
    const val RATE_LIMIT_REQUESTS = 10
    const val RATE_LIMIT_WINDOW_SECONDS = 60
}
