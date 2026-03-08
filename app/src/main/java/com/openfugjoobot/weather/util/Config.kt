package com.openfugjoobot.weather.util

/**
 * App configuration
 */
object Config {
    // Default station: Neumarkt (ISTAT 021029)
    const val DEFAULT_STATION_CODE = "021029"
    
    // Cache duration in minutes
    const val CACHE_VALIDITY_MINUTES = 30L  // Cache valid for 30 minutes
    
    // Network retry settings
    const val MAX_NETWORK_RETRIES = 3
    
    // Debug mode
    const val API_DEBUG = true  // Log raw API responses
}
