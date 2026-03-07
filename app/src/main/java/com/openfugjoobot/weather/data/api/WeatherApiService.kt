package com.openfugjoobot.weather.data.api

import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit service interface for OpenDataHub Tourism API v1
 * 
 * API: https://tourism.api.opendatahub.com/v1/Weather/Forecast
 * Returns list of all weather forecasts (filter by MunicipalityIstatCode client-side)
 */
interface WeatherApiService {
    
    /**
     * Get all weather forecasts
     * Filter by station code in the repository layer
     */
    @GET(ApiConstants.ENDPOINT_WEATHER_FORECAST)
    suspend fun getWeatherForecast(): Response<List<ApiWeatherForecast>>
}

/**
 * Weather forecast data from OpenDataHub API
 */
data class ApiWeatherForecast(
    val _Meta: MetaInfo,
    val Self: String,
    val Id: String,
    val Date: String, // ISO 8601
    val Shortname: String, // e.g., "Neumarkt-Egna"
    val MunicipalityIstatCode: String, // e.g., "021059"
    val MunicipalityName: MunicipalityName,
    val ForeCastDaily: List<ForecastDaily>,
    val GpsInfo: GpsInfo?
)

data class MetaInfo(
    val Id: String,
    val Type: String,
    val LastUpdate: String,
    val Source: String
)

data class MunicipalityName(
    val en: String? = null,
    val de: String? = null,
    val it: String? = null
)

data class GpsInfo(
    val Latitude: Double?,
    val Longitude: Double?,
    val Altitude: Int?
)

data class ForecastDaily(
    val Date: String, // ISO 8601
    val MinTemp: Int,
    val MaxTemp: Int,
    val SunshineDuration: Int?, // hours
    val PrecipitationProbability: Int, // percentage
    val Precipitation: Int?, // mm
    val WeatherCode: String, // single char: a=sunny, b=partly cloudy, c=cloudy, etc.
    val WeatherDesc: String,
    val WeatherDescription: Description,
    val WeatherImgUrl: String
)

data class Description(
    val en: String? = null,
    val de: String? = null,
    val it: String? = null
)
