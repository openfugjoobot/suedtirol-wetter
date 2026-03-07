package com.openfugjoobot.weather.data.api

import com.openfugjoobot.weather.domain.model.ForecastDay
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for OpenDataHub Tourism API v1
 */
interface WeatherApiService {
    
    /**
     * Get weather forecast for a specific station
     * @param sCode Station code (e.g., "021029" for Neumarkt Egna)
     * @param limit Maximum number of forecast entries
     * @param orderby Sorting order
     */
    @GET(ApiConstants.ENDPOINT_WEATHER_FORECAST)
    suspend fun getWeatherForecast(
        @Query(ApiConstants.PARAM_STATION_CODE) sCode: String,
        @Query(ApiConstants.PARAM_LIMIT) limit: Int = ApiConstants.DEFAULT_LIMIT,
        @Query(ApiConstants.PARAM_ORDERBY) orderby: String = ApiConstants.DEFAULT_ORDERBY
    ): Response<WeatherApiResponse>
}

/**
 * API response wrapper for OpenDataHub
 */
data class WeatherApiResponse(
    val data: List<ApiWeatherData>,
    val nextPage: String? = null
)

data class ApiWeatherData(
    val sCode: String,
    val sName: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Int?,
    val temperature: ApiTemperature?,
    val weatherCondition: String?,
    val weatherConditionType: String?,
    val epoch: Long
)

data class ApiTemperature(
    val value: Double?
)
