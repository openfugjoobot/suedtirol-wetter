package com.openfugjoobot.weather.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CurrentWeatherCard(
    uiState: WeatherUiState,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is WeatherUiState.Idle -> {
                    Text("Idle")
                }
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is WeatherUiState.Success -> {
                    Text(
                        text = uiState.forecast.stationName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.forecast.currentTemperature.current?.let { temp ->
                        Text(
                            text = String.format(java.util.Locale.getDefault(), "%.1f°C", temp),
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                    Text(
                        text = uiState.forecast.currentCondition.description ?: "Unknown",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is WeatherUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
