package com.openfugjoobot.weather.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openfugjoobot.weather.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    stationCode: String = com.openfugjoobot.weather.util.Config.DEFAULT_STATION_CODE
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CurrentWeatherCard(
                uiState = uiState,
                onRefresh = { } // TODO: Implement refresh
            )
            
            ForecastList(uiState = uiState)
        }
    }
}
