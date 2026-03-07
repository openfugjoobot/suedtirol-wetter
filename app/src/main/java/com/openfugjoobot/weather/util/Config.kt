package com.openfugjoobot.weather.util

/**
 * App configuration
 */
object Config {
    // Default station: Neumarkt (ISTAT 021029)
    const val DEFAULT_STATION_CODE = "021029"
    
    // Cache duration in minutes (TEMPORARILY DISABLED FOR DEBUGGING)
    // const val CACHE_VALIDITY_MINUTES = 30L
    const val CACHE_VALIDITY_MINUTES = 0L  // Always fetch fresh data
    
    // Network retry settings
    const val MAX_NETWORK_RETRIES = 3
    
    // Debug mode
    const val API_DEBUG = true  // Log raw API responses
}
