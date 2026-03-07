package com.openfugjoobot.weather.data.api

/**
 * API constants for OpenDataHub Tourism API v1
 */
object ApiConstants {
    const val BASE_URL = "https://databrowser.opendatahub.com"
    const val ENDPOINT_WEATHER_FORECAST = "/dataset/table/tourism/v1/Weather/Forecast"
    
    // Query parameters
    const val PARAM_STATION_CODE = "sCode"
    const val PARAM_LIMIT = "limit"
    const val PARAM_ORDERBY = "orderby"
    
    // Default values
    const val DEFAULT_LIMIT = 10
    const val DEFAULT_ORDERBY = "EpochAscending"
}
