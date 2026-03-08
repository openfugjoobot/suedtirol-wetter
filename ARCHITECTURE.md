# Architecture Overview - Weather Dashboard APK

## 1. System Architecture

### Architectural Pattern: MVVM + Clean Architecture

The Weather Dashboard follows **Clean Architecture** principles with three distinct layers:

```
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Screens   │  │ Components  │  │       ViewModel         │  │
│  │  (Compose)   │  │  (Compose)  │  │  (StateFlow + Hilt)     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                        DATA LAYER                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │  Repository │  │  Retrofit   │  │         DTOs             │ │
│  │  (Impl)     │  │   Client    │  │   (Data Transfer Objects)│ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
└────────────────────────────┬────────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────────┐
│                       DOMAIN LAYER                             │
│  ┌─────────────┐  ┌─────────────────────────────────────────┐  │
│  │   Models   │  │        Repository Interface              │  │
│  │ (Kotlin)   │  │        (Abstraction)                    │  │
│  └─────────────┘  └─────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## 2. Layer Details

### Domain Layer

The innermost layer with **no external dependencies**. Contains business logic and entities.

| Component | Description |
|-----------|-------------|
| `WeatherForecast` | Root domain model |
| `Temperature` | Temperature data (min/max/current) |
| `WeatherCondition` | Qualitative weather description |
| `ForecastDay` | Single day forecast entry |
| `WeatherRepository` | Repository interface (abstraction) |

**Key Principle:** Domain models are pure Kotlin data classes with no Android/framework imports.

### Data Layer

Handles data retrieval from external sources. Implements repository interfaces defined in the domain layer.

| Component | Description |
|-----------|-------------|
| `WeatherApiService` | Retrofit interface for API calls |
| `RetrofitClient` | HTTP client configuration |
| `WeatherForecastDto` | API response data transfer objects |
| `ForecastMapper` | DTO → Domain model transformation |
| `WeatherRepositoryImpl` | Repository implementation |

**Data Flow:**
```
API Response (JSON) → DTOs → Mapper → Domain Models → Repository → ViewModel
```

### Presentation Layer

UI components and state management.

| Component | Description |
|-----------|-------------|
| `WeatherScreen` | Main screen composable |
| `CurrentWeatherCard` | Current conditions display |
| `ForecastList` | 7-day forecast list |
| `WeatherViewModel` | UI state management |
| `WeatherUiState` | Sealed class for state |

## 3. State Management

### StateFlow Pattern

The app uses Kotlin **StateFlow** for reactive state management:

```kotlin
// ViewModel
private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

// UI observes state
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is WeatherUiState.Loading -> LoadingIndicator()
        is WeatherUiState.Success -> WeatherContent(state.data)
        is WeatherUiState.Error -> ErrorMessage(state.message)
    }
}
```

### State States

```
┌────────────────────────────────────────────────────────────┐
│                      UI State Flow                        │
├────────────────────────────────────────────────────────────┤
│                                                            │
│   ┌──────────┐      ┌──────────┐      ┌──────────┐       │
│   │ Loading  │ ───▶ │ Success  │ ───▶ │  Error   │       │
│   └──────────┘      └──────────┘      └──────────┘       │
│        ▲                │                   │            │
│        │                ▼                   ▼            │
│        └────────────  (Refresh)  ──────▶  Retry          │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

## 4. Dependency Injection (Hilt)

The app uses **Hilt** for dependency injection with compile-time safety.

### Module Structure

```
┌─────────────────────────────────────────────────────────┐
│                    Hilt Modules                         │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │  AppModule     │    │    NetworkModule            │ │
│  │  - Application │    │    - Retrofit               │ │
│  │  - Context     │    │    - OkHttpClient            │ │
│  └─────────────────┘    │    - LoggingInterceptor    │ │
│                         └─────────────────────────────┘ │
│                                                          │
│  ┌─────────────────────────────────────────────────────┐│
│  │           RepositoryModule                          ││
│  │    - WeatherRepository (interface → impl)          ││
│  └─────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

### Injection Points

```kotlin
// Application
@HiltAndroidApp
class WeatherApplication : Application()

// Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// ViewModel
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel()
```

## 5. Network Layer

### Retrofit Configuration

```kotlin
// Base URL
private const val BASE_URL = "https://databrowser.opendatahub.com/dataset/table/"

// Client setup
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor())
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

### API Interface

```kotlin
interface WeatherApiService {
    @GET("tourism/v1/Weather/Forecast")
    suspend fun getWeatherForecast(
        @Query("sCode") sCode: String,
        @Query("limit") limit: Int = 100,
        @Query("orderby") orderby: String = "mvalidtime desc"
    ): Response<List<WeatherForecastDto>>
}
```

## 6. Repository Pattern

### Interface (Domain Layer)

```kotlin
interface WeatherRepository {
    suspend fun getWeatherForecast(stationCode: String): Result<WeatherForecast>
}
```

### Implementation (Data Layer)

```kotlin
class WeatherRepositoryImpl : WeatherRepository {
    
    private val apiService = RetrofitClient.weatherApiService
    
    override suspend fun getWeatherForecast(stationCode: String): Result<WeatherForecast> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeatherForecast(stationCode)
                
                if (response.isSuccessful) {
                    val dtoList = response.body() ?: emptyList()
                    val domainModel = dtoList.toDomainModel()
                    Result.success(domainModel)
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
```

## 7. UI Component Hierarchy

```
MainActivity
└── WeatherScreen (Scaffold)
    ├── TopAppBar
    │   └── RefreshAction
    ├── LoadingIndicator (CircularProgressIndicator)
    ├── ErrorCard (on error state)
    └── WeatherContent (LazyColumn)
        ├── CurrentWeatherCard (Card)
        │   ├── StationName
        │   ├── WeatherIcon
        │   ├── Temperature Display
        │   └── LastUpdated
        └── ForecastList (LazyColumn)
            └── ForecastItem (repeat for 7 days)
                ├── DayName
                ├── WeatherIcon
                └── TemperatureRange
```

## 8. Data Flow

### Fetch Weather Data

```
User opens app
       │
       ▼
MainActivity created
       │
       ▼
WeatherViewModel.init { fetchWeather() }
       │
       ▼
Repository.getWeatherForecast("021029")
       │
       ▼
WeatherApiService.getWeatherForecast()
       │
       ▼
Retrofit → OkHttp → HTTPS → OpenDataHub API
       │
       ▼
Parse JSON → DTOs → Map to Domain Models
       │
       ▼
Update StateFlow with Success(data)
       │
       ▼
Compose UI recomposes → Show weather
```

### Error Handling Flow

```
API Request
     │
     ▼
┌────────────┐
│ Success?   │──Yes──▶ Display Data
└─────┬──────┘
      │ No
      ▼
┌────────────┐
│ Error Type │
└─────┬──────┘
      │
      ├─Network──▶ Show "No internet"
      ├─4xx──────▶ Show "Request error"
      ├─5xx──────▶ Show "Server error" + Retry button
      └─Parse────▶ Show "Data error"
```

## 9. Project Structure

```
app/src/main/java/com/openfugjoobot/weather/
│
├── WeatherApplication.kt          # Hilt entry point
├── MainActivity.kt                # Single Activity
│
├── data/
│   ├── api/
│   │   ├── WeatherApiService.kt   # Retrofit interface
│   │   └── ApiClient.kt          # HTTP client setup
│   ├── dto/
│   │   └── WeatherForecastDto.kt
│   ├── mapper/
│   │   └── ForecastMapper.kt     # DTO → Domain
│   └── repository/
│       └── WeatherRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   ├── WeatherForecast.kt
│   │   ├── Temperature.kt
│   │   ├── WeatherCondition.kt
│   │   └── ForecastDay.kt
│   └── repository/
│       └── WeatherRepository.kt  # Interface
│
├── presentation/
│   ├── ui/
│   │   ├── screens/
│   │   │   └── WeatherScreen.kt
│   │   ├── components/
│   │   │   ├── CurrentWeatherCard.kt
│   │   │   └── ForecastList.kt
│   │   └── theme/
│   │       ├── Theme.kt
│   │       └── Color.kt
│   └── viewmodel/
│       ├── WeatherViewModel.kt
│       └── WeatherUiState.kt
│
└── di/
    ├── AppModule.kt
    └── NetworkModule.kt
```

## 10. Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **MVVM + Clean** | Clear separation of concerns; testable; maintainable |
| **Jetpack Compose** | Declarative UI; less boilerplate; modern Android standard |
| **StateFlow** | Lifecycle-aware; coroutine-friendly; hot stream |
| **Hilt** | Compile-time DI; reduced boilerplate; Android-optimized |
| **Retrofit + OkHttp** | Industry standard; type-safe; extensible |
| **Repository Pattern** | Abstraction over data sources; easy to swap implementations |

## 11. Testing Strategy

### Unit Tests
- **ViewModel** - Test state transitions and business logic
- **Repository** - Test data mapping and error handling
- **Mappers** - Test DTO to domain transformations

### Integration Tests
- **API Integration** - Test actual API calls (requires network)

### UI Tests
- **Compose Tests** - Test UI rendering with mock data

## 12. Future Enhancements

| Feature | Description |
|---------|-------------|
| **Offline Caching** | Room database for persistent storage |
| **Multiple Stations** | Allow user to select different weather stations |
| **Localization** | German/Italian support for South Tyrol |
| **Widgets** | Home screen weather widget |
| **Notifications** | Severe weather alerts |

## 13. Related Documentation

- [API Documentation](API.md) - OpenDataHub endpoints
- [Setup Guide](SETUP.md) - Development environment
- [README.md](README.md) - Project overview
