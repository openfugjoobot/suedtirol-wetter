package com.openfugjoobot.weather.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openfugjoobot.weather.domain.model.ForecastDay

@Composable
fun ForecastList(uiState: WeatherUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Forecast",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            when (uiState) {
                is WeatherUiState.Success -> {
                    val forecast = uiState.forecast.forecast
                    if (forecast.isEmpty()) {
                        Text("No forecast data available")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(forecast) { day ->
                                ForecastItem(day)
                            }
                        }
                    }
                }
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is WeatherUiState.Error -> {
                    Text("Error loading forecast")
                }
                is WeatherUiState.Idle -> {
                    Text("No data")
                }
            }
        }
    }
}

@Composable
fun ForecastItem(day: ForecastDay) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day.date.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = day.condition.description ?: "Unknown",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${day.temperature.maximum ?: "--"}° / ${day.temperature.minimum ?: "--"}°",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
