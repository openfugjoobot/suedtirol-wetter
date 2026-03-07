package com.openfugjoobot.weather.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.openfugjoobot.weather.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    stationCode: String = com.openfugjoobot.weather.util.Config.DEFAULT_STATION_CODE
) {
    val uiState by viewModel.uiState.collectAsState()
    var tapCount by remember { mutableStateOf(0) }
    
    LaunchedEffect(tapCount) {
        if (tapCount >= 2) {
            // Double tap detected - force refresh
            viewModel.refreshWeather()
            tapCount = 0
        }
    }
    
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
            // Add clickable wrapper with double-tap detection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(tapCount) {
                        detectTapGestures(
                            onTap = { tapCount++ }
                        )
                    }
            ) {
                CurrentWeatherCard(
                    uiState = uiState
                )
            }
            
            ForecastList(uiState = uiState)
        }
    }
}
