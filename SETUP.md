# Development Setup Guide

This guide covers setting up the Weather Dashboard APK development environment.

## Prerequisites

### Required Software

| Software | Version | Notes |
|----------|---------|-------|
| JDK | 17+ | LTS recommended |
| Android Studio | Hedgehog (2024.1)+ | Latest stable |
| Android SDK | API 24+ (Android 7.0) | Min SDK |
| Gradle | 8.4+ | Bundled or wrapper |
| Kotlin | 1.9+ | Bundled with Gradle |

### Verify Installation

```bash
# Check Java
java -version

# Check Gradle
./gradlew -v

# Check Android SDK
echo $ANDROID_HOME
# or
ls $ANDROID_HOME/platforms/
```

## Project Setup

### 1. Clone Repository

```bash
git clone <repository-url>
cd weather-dashboard
```

### 2. Open in Android Studio

1. Launch Android Studio
2. `File → Open`
3. Select `weather-dashboard` directory
4. Wait for Gradle sync to complete

### 3. Configure SDK

If prompted:
1. Click "Setup SDK" in the notification
2. Select your installed Android SDK
3. Accept licenses

Or manually in `app/build.gradle.kts`:
```kotlin
android {
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
}
```

## Build Project

### Debug Build

```bash
./gradlew assembleDebug
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

**Output:** `app/build/outputs/apk/release/app-release.apk`

### Clean Build

```bash
./gradlew clean assembleDebug
```

## Run on Device/Emulator

### Using Android Studio

1. Connect device or start emulator
2. Select target in dropdown
3. Press `Run → Run 'app'` (or `Shift + F10`)

### Using Command Line

```bash
# Install APK to connected device
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall
adb uninstall com.openfugjoobot.weather
```

## Development Workflow

### Code Generation

The project uses Hilt for dependency injection. After modifying Hilt modules:

```bash
./gradlew kapt
```

Or rebuild the project.

### Running Tests

```bash
# Unit tests
./gradlew test

# UI tests
./gradlew connectedAndroidTest
```

### Linting

```bash
./gradlew lint
```

## Common Issues

### Gradle Sync Fails

**Problem:** `Gradle sync failed: Could not resolve dependencies`

**Solution:**
1. Clear Gradle cache: `./gradlew clean`
2. Delete `.gradle` directory in project root
3. Restart Android Studio and sync again

### SDK Not Found

**Problem:** `SDK location not found`

**Solution:**
1. Set `ANDROID_HOME` environment variable:
   ```bash
   export ANDROID_HOME=/path/to/android/sdk
   ```
2. Or configure in `local.properties`:
   ```
   sdk.dir=/path/to/android/sdk
   ```

### Java Version Mismatch

**Problem:** `Java version mismatch`

**Solution:**
1. Check Java: `java -version`
2. Set in `gradle.properties`:
   ```properties
   org.gradle.java.home=/path/to/jdk17
   ```

### Network/Proxy Issues

**Problem:** Cannot download dependencies

**Solution:**
Configure proxy in `gradle.properties`:
```properties
systemProp.http.proxyHost=proxy.example.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.example.com
systemProp.https.proxyPort=8080
```

## Project Structure Overview

```
app/src/main/
├── java/com/openfugjoobot/weather/
│   ├── data/           # API, DTOs, Repository impl
│   ├── domain/         # Models, Repository interfaces
│   ├── presentation/  # ViewModels, UI
│   ├── di/             # Hilt modules
│   └── util/           # Helpers
├── res/                # Resources (layouts, values, drawables)
└── AndroidManifest.xml
```

## Key Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Compose BOM | 2024.02.00 | UI toolkit |
| Hilt | 2.50 | Dependency injection |
| Retrofit | 2.9.0 | HTTP client |
| OkHttp | 4.12.0 | HTTP interceptors |
| Coroutines | 1.7.3 | Async operations |
| Material3 | (via BOM) | Design system |

## Next Steps

1. Review [API.md](API.md) - Understand the OpenDataHub endpoints
2. Review [ARCHITECTURE.md](ARCHITECTURE.md) - System design
3. Start developing: Pick an issue from the backlog

## Additional Resources

- [Android Developer Docs](https://developer.android.com/docs)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Hilt Guide](https://developer.android.com/training/dependency-injection/hilt-android)
