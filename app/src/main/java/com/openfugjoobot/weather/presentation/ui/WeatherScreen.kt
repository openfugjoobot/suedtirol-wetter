package com.openfugjoobot.weather.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openfugjoobot.weather.presentation.viewmodel.WeatherViewModel
import com.openfugjoobot.weather.presentation.ui.components.StationPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    stationCode: String = com.openfugjoobot.weather.util.Config.DEFAULT_STATION_CODE
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Menu state
    var showMenu by remember { mutableStateOf(false) }
    var showStationPicker by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Südtirol Wetter",
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                actions = {
                    // Three dots menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menü"
                            )
                        }
                        
                        // Dropdown menu
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ort wählen...") },
                                onClick = {
                                    showMenu = false
                                    showStationPicker = true
                                },
                                leadingIcon = {
                                    Icon(
                                        androidx.compose.material.icons.Icons.Default.LocationOn,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                    
                    // Refresh button
                    IconButton(onClick = { viewModel.loadWeather() }) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Refresh,
                            contentDescription = "Aktualisieren"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Version badge (DEBUG)
            Text(
                text = "⚠️ v2.1.2 | istatCode Fix | Menü: ⋮",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            CurrentWeatherCard(
                uiState = uiState
            )
            
            ForecastList(uiState = uiState)
        }
    }
    
    // Station Picker Dialog
    if (showStationPicker) {
        StationPickerDialog(
            currentStationCode = stationCode,
            onStationSelected = { code, name ->
                viewModel.changeStation(code)
                showStationPicker = false
            },
            onDismiss = { showStationPicker = false }
        )
    }
}
