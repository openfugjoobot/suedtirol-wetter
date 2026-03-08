# Weather Dashboard APK

Android weather application for Neumarkt/Egna (South Tyrol, Italy) displaying current conditions and 7-day forecast using the OpenDataHub Tourism API.

## Overview

| Property | Value |
|----------|-------|
| **App Name** | Neumarkt Weather |
| **Package** | `com.openfugjoobot.weather` |
| **Platform** | Android (API 24+) |
| **Language** | Kotlin 1.9 |
| **Architecture** | MVVM + Clean Architecture |

## Features

- **Current Weather** - Temperature range and qualitative forecast
- **7-Day Forecast** - Daily min/max temperatures and conditions
- **Pull-to-Refresh** - Manual data refresh
- **Offline Support** - Cached data with 5-minute TTL
- **Material Design 3** - Modern Android UI

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose + Material 3 |
| State Management | StateFlow + ViewModel |
| Dependency Injection | Hilt |
| Networking | Retrofit + OkHttp |
| Async | Kotlin Coroutines |
| Architecture | MVVM + Repository Pattern |

## API

- **Provider:** OpenDataHub Tourism API v1
- **Endpoint:** `https://databrowser.opendatahub.com/dataset/table/tourism/v1/Weather/Forecast`
- **Station Code:** `021029` (Neumarkt/Egna)
- **Authentication:** None required (public API)

See [API.md](API.md) for detailed endpoint documentation.

## Project Structure

```
weather-dashboard/
├── app/
│   └── src/main/java/com/openfugjoobot/weather/
│       ├── data/                    # Data layer
│       │   ├── api/                 # Retrofit API client
│       │   ├── dto/                 # Data transfer objects
│       │   ├── mapper/              # DTO to domain mappers
│       │   └── repository/         # Repository implementation
│       ├── domain/                  # Domain layer
│       │   ├── model/               # Domain models
│       │   └── repository/          # Repository interfaces
│       ├── presentation/            # Presentation layer
│       │   ├── ui/                  # Compose UI components
│       │   │   ├── screens/         # Screen composables
│       │   │   ├── components/      # Reusable UI components
│       │   │   └── theme/           # Material 3 theming
│       │   └── viewmodel/           # ViewModels
│       ├── di/                      # Hilt modules
│       └── util/                    # Utilities
├── build.gradle.kts                 # Root build config
└── settings.gradle.kts             # Project settings
```

## Quick Start

### Prerequisites

- Android Studio Hedgehog (2024.1) or newer
- JDK 17+
- Android SDK (API 24+)

### Build & Run

1. **Clone and open**
   ```bash
   cd weather-dashboard
   open in Android Studio
   ```

2. **Sync Gradle**
   - Android Studio will prompt to sync
   - Or: `File → Sync Project with Gradle Files`

3. **Run**
   - Select device/emulator
   - Press `Run → Run 'app'`

### Build APK

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

**APK Location:** `app/build/outputs/apk/debug/app-debug.apk`

## Development

See [SETUP.md](SETUP.md) for detailed development environment setup.

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for architecture documentation.

## Configuration

### Station Code

The station code is configured in:
- `WeatherViewModel.kt` - Hardcoded as `"021029"`
- To change: modify the station code in the ViewModel

### Cache Duration

Default cache TTL is 5 minutes. Configurable in `WeatherRepositoryImpl.kt`.

## Known Issues

- Forecast mapping from API is incomplete (Phase 5 QA)
- Null safety improvements needed
- Logging interceptor enabled in production (should use BuildConfig.DEBUG)

## Contributing

1. Create a feature branch
2. Make changes
3. Submit PR for review

## License

MIT License - See LICENSE file for details.

## Resources

- [OpenDataHub Documentation](https://opendatahub.com/documentation/)
- [Jetpack Compose Docs](https://developer.android.com/compose)
- [Hilt Documentation](https://dagger.dev/hilt)
