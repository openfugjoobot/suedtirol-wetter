# API Documentation - OpenDataHub Integration

This document covers the OpenDataHub API endpoints used by the Weather Dashboard APK.

## Base URL

```
https://databrowser.opendatahub.com/dataset/table/
```

## Endpoints

### Weather Forecast

**Endpoint:** `/tourism/v1/Weather/Forecast`

**Method:** `GET`

**Description:** Returns weather forecast data for a specific station.

#### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `sCode` | string | Yes | - | Station code (e.g., `021029` for Neumarkt/Egna) |
| `limit` | integer | No | 100 | Maximum records to return |
| `orderby` | string | No | `mvalidtime desc` | Sort order |

#### Request Example

```bash
curl "https://databrowser.opendatahub.com/dataset/table/tourism/v1/Weather/Forecast?sCode=021029&limit=100&orderby=mvalidtime%20desc"
```

#### Response Format

```json
[
  {
    "scode": "021029",
    "sname": "Neumarkt/Egna",
    "mvalidtime": "2024-03-07T12:00:00Z",
    "tvalue": 17.5,
    "mvalue": "partly cloudy",
    "mname": "forecast",
    "tname": "qualitative-forecast",
    "mperiod": 1,
    "scoordinate": {
      "y": 46.32,
      "x": 11.21
    }
  }
]
```

## Station Information

### Neumarkt/Egna

| Property | Value |
|----------|-------|
| **Station Code** | `021029` |
| **Station Name** | Neumarkt/Egna |
| **Coordinates** | 46.32°N, 11.21°E |
| **Altitude** | ~210m |

## Data Types

The API returns multiple measurement types per station. Key types:

| Type Name (`tname`) | Description | Unit |
|---------------------|-------------|------|
| `qualitative-forecast` | Weather condition description | Text |
| `forecast-air-temperature-max` | Maximum temperature | °C |
| `forecast-air-temperature-min` | Minimum temperature | °C |
| `forecast-air-temperature-instant` | Current temperature | °C |
| `forecast-precipitation-probability` | Rain probability | % |
| `forecast-wind-speed-10m` | Wind speed | km/h |

## Weather Conditions

Qualitative forecast values:

| Value | Icon |
|-------|------|
| `sunny` | ☀️ Clear |
| `partly cloudy` | ⛅ Partly Cloudy |
| `cloudy` | ☁️ Cloudy |
| `rainy` | 🌧️ Rain |
| `snowy` | ❄️ Snow |
| `stormy` | ⛈️ Storm |

## Filtering Examples

### Get Latest Temperature

```bash
curl "https://databrowser.opendatahub.com/dataset/table/tourism/v1/Weather/Forecast?sCode=021029&tname=forecast-air-temperature-max&limit=1&orderby=mvalidtime%20desc"
```

### Get Qualitative Forecast

```bash
curl "https://databrowser.opendatahub.com/dataset/table/tourism/v1/Weather/Forecast?sCode=021029&tname=qualitative-forecast&limit=1&orderby=mvalidtime%20desc"
```

### Date Range Filter

```bash
curl "https://databrowser.opendatahub.com/dataset/table/tourism/v1/Weather/Forecast?sCode=021029&from=2024-03-07&to=2024-03-14"
```

## Android Integration

### Retrofit Interface

```kotlin
interface WeatherApiService {
    
    @GET("tourism/v1/Weather/Forecast")
    suspend fun getWeatherForecast(
        @Query("sCode") sCode: String = "021029",
        @Query("limit") limit: Int = 100,
        @Query("orderby") orderby: String = "mvalidtime desc"
    ): Response<List<WeatherForecastDto>>
}
```

### Response DTO

```kotlin
data class WeatherForecastDto(
    @SerializedName("scode") val stationCode: String?,
    @SerializedName("sname") val stationName: String?,
    @SerializedName("mvalidtime") val validTime: String?,
    @SerializedName("tvalue") val temperatureValue: Double?,
    @SerializedName("mvalue") val measurementValue: Any?,
    @SerializedName("mname") val measurementName: String?,
    @SerializedName("tname") val typeName: String?,
    @SerializedName("mperiod") val period: Int?,
    @SerializedName("scoordinate") val coordinates: CoordinateDto?
)
```

## Rate Limits

- **Public Access:** No strict rate limit
- **Recommended Polling:** Every 15-30 minutes
- **Update Frequency:** Backend models update every 6-12 hours

## Error Handling

| Error Code | Description |
|------------|-------------|
| 200 | Success |
| 400 | Bad request (invalid station code) |
| 404 | No data found |
| 500 | Server error |

### Error Response

```json
{
  "error": "Not Found",
  "message": "No data available for station 021029"
}
```

## Data Mapping

### API Response → Domain Model

```kotlin
fun List<WeatherForecastDto>.toDomainModel(): WeatherForecast {
    val tempRecords = filter { 
        it.typeName?.contains("temperature", ignoreCase = true) == true 
    }
    
    val minTemp = tempRecords.find { it.typeName?.contains("min") == true }?.temperatureValue
    val maxTemp = tempRecords.find { it.typeName?.contains("max") == true }?.temperatureValue
    
    val condition = find { 
        it.typeName?.contains("qualitative") == true 
    }?.let {
        WeatherCondition(description = it.measurementValue as? String ?: "Unknown")
    }
    
    return WeatherForecast(
        stationCode = firstOrNull()?.stationCode ?: "021029",
        stationName = firstOrNull()?.stationName ?: "Neumarkt/Egna",
        timestamp = System.currentTimeMillis(),
        temperature = Temperature(min = minTemp, max = maxTemp),
        weatherCondition = condition,
        forecastDays = emptyList() // TODO: Implement
    )
}
```

## Testing with Postman/cURL

### Quick Test

```bash
# Get weather data
curl -X GET "https://databrowser.opendatahub.com/dataset/table/tourism/v1/Weather/Forecast?sCode=021029&limit=10" \
  -H "Accept: application/json"
```

### Expected Response (Success)

```json
[
  {
    "scode": "021029",
    "sname": "Neumarkt/Egna",
    "mvalidtime": "2024-03-09T23:00:00.000Z",
    "tvalue": 17,
    "mvalue": "partly cloudy",
    "mname": "weather-forecast",
    "tname": "qualitative-forecast",
    "mperiod": 1,
    "scoordinate": {"y": 46.32, "x": 11.21}
  },
  {
    "scode": "021029",
    "sname": "Neumarkt/Egna",
    "mvalidtime": "2024-03-09T23:00:00.000Z",
    "tvalue": 17,
    "mname": "air-temperature",
    "tname": "forecast-air-temperature-max",
    "mperiod": 1,
    "scoordinate": {"y": 46.32, "x": 11.21}
  }
]
```

## References

- [OpenDataHub Documentation](https://opendatahub.com/documentation/)
- [Tourism API Guide](https://docs.opendatahub.com/tourism)
- [Mobility API v2](https://mobility.api.opendatahub.com/v2) (alternative)
