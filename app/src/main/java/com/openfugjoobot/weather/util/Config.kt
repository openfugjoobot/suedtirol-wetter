package com.openfugjoobot.weather.util

/**
 * App configuration constants
 */
object Config {
    /**
     * Default station code for Neumarkt/Egna
     * Can be overridden by user preferences
     */
    const val DEFAULT_STATION_CODE = "021029"
    
    /**
     * Cache validity in minutes
     */
    const val CACHE_VALIDITY_MINUTES = 5L
    
    /**
     * Maximum retry attempts for network requests
     */
    const val MAX_NETWORK_RETRIES = 3
}
