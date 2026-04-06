# FeedRight Android App

Native Android app for Purina salesmen to record farm visits offline and sync to the backend.

## Status

**🚧 UNDER DEVELOPMENT**

This is the project structure and architecture documentation. The app requires Android Studio to build and run.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (modern declarative UI)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Local Database:** Room (SQLite)
- **Network:** Retrofit + OkHttp
- **Dependency Injection:** Hilt
- **Background Work:** WorkManager
- **Async:** Kotlin Coroutines + Flow
- **Min SDK:** 24 (Android 7.0+, covers 95%+ devices)
- **Target SDK:** 34 (Android 14)

## Prerequisites

### Required

- **Android Studio** (Hedgehog | 2023.1.1 or later)
- **JDK 17**
- Android SDK with:
  - Android 14 (API 34) - for compilation
  - Android 7.0 (API 24) - minimum supported version

### Backend Must Be Running

The app connects to:
- **URL:** `http://10.0.2.2:8080/api/` (Android emulator localhost alias)
- **For physical device:** Update `API_BASE_URL` in `app/build.gradle.kts` to your computer's IP

## Project Structure

```
android-app/
├── app/
│   ├── build.gradle.kts           ✅ App-level Gradle config
│   ├── src/main/
│   │   ├── AndroidManifest.xml    ✅ App configuration & permissions
│   │   ├── java/com/purina/feedright/
│   │   │   ├── FeedRightApplication.kt          ✅ Application class (Hilt)
│   │   │   ├── MainActivity.kt                  ✅ Main entry point
│   │   │   │
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   ├── VisitEntity.kt       ✅ Room entity
│   │   │   │   │   │   ├── FarmEntity.kt        ✅ Room entity
│   │   │   │   │   │   └── ProductEntity.kt     ✅ Room entity
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   └── VisitDao.kt          ✅ Room DAO
│   │   │   │   │   └── FeedRightDatabase.kt     ✅ Room database
│   │   │   │   │
│   │   │   │   ├── remote/
│   │   │   │   │   ├── FeedRightApi.kt          ✅ Retrofit API interface
│   │   │   │   │   └── dto/                     📋 DTOs (to create)
│   │   │   │   │
│   │   │   │   └── repository/
│   │   │   │       ├── VisitRepository.kt       📋 To create
│   │   │   │       ├── FarmRepository.kt        📋 To create
│   │   │   │       └── ProductRepository.kt     📋 To create
│   │   │   │
│   │   │   ├── di/                              📋 Hilt modules (to create)
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   ├── NetworkModule.kt
│   │   │   │   └── RepositoryModule.kt
│   │   │   │
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── login/                   📋 Login screen (to create)
│   │   │   │   │   ├── home/                    📋 Home/Visit list (to create)
│   │   │   │   │   └── visit/                   📋 Visit recording (to create)
│   │   │   │   ├── components/                  📋 Reusable UI (to create)
│   │   │   │   ├── navigation/                  📋 Nav graph (to create)
│   │   │   │   └── theme/                       📋 Material theme (to create)
│   │   │   │
│   │   │   └── worker/
│   │   │       └── SyncWorker.kt                📋 WorkManager sync (to create)
│   │   │
│   │   └── res/
│   │       ├── values/
│   │       │   ├── strings.xml                  📋 To create
│   │       │   ├── colors.xml                   📋 To create
│   │       │   └── themes.xml                   📋 To create
│   │       └── drawable/                        📋 Icons (to create)
│   │
│   └── proguard-rules.pro                       📋 To create
│
├── build.gradle.kts                             ✅ Project-level Gradle
├── settings.gradle.kts                          ✅ Gradle settings
├── gradle.properties                            📋 To create
└── README.md                                    ✅ This file

✅ Created   📋 To be created
```

## Architecture Overview

### MVVM + Repository Pattern

```
┌─────────────────────────────────────────────┐
│              UI Layer (Compose)              │
│  - LoginScreen, HomeScreen, VisitScreen     │
└────────────────┬────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────┐
│           ViewModel Layer                    │
│  - LoginViewModel, HomeViewModel, etc.      │
│  - Handles UI state & business logic        │
└────────────────┬────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────┐
│          Repository Layer                    │
│  - Single source of truth                   │
│  - Coordinates local + remote data          │
└────────┬────────────────────┬───────────────┘
         │                    │
         ▼                    ▼
┌────────────────┐   ┌───────────────────┐
│  Room Database │   │  Retrofit API     │
│  (Offline)     │   │  (Online)         │
└────────────────┘   └───────────────────┘
```

### Offline-First Flow

1. **User records visit** → Saved to Room database immediately
2. **Mark as unsynced** → `isSynced = false`
3. **WorkManager triggers** → Every 15 minutes when online
4. **Batch sync** → Send all unsynced visits to backend
5. **Mark as synced** → Update `isSynced = true`, set `syncedAt`

## Key Files to Create

### 1. Dependency Injection (Hilt Modules)

**`di/DatabaseModule.kt`**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FeedRightDatabase {
        return Room.databaseBuilder(
            context,
            FeedRightDatabase::class.java,
            FeedRightDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideVisitDao(database: FeedRightDatabase) = database.visitDao()
}
```

**`di/NetworkModule.kt`**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFeedRightApi(retrofit: Retrofit): FeedRightApi {
        return retrofit.create(FeedRightApi::class.java)
    }
}
```

### 2. Repository Layer

**`repository/VisitRepository.kt`**
```kotlin
class VisitRepository @Inject constructor(
    private val visitDao: VisitDao,
    private val api: FeedRightApi
) {
    val recentVisits: Flow<List<VisitEntity>> = visitDao.getRecentVisits()

    suspend fun recordVisit(visit: VisitEntity) {
        visitDao.insertVisit(visit)
    }

    suspend fun syncVisits(): SyncResponse {
        val unsyncedVisits = visitDao.getUnsyncedVisits()
        if (unsyncedVisits.isEmpty()) {
            return SyncResponse(0, 0, emptyList())
        }

        val dtos = unsyncedVisits.map { it.toDto() }
        val response = api.syncVisits(SyncRequest(dtos))

        // Mark synced visits
        if (response.synced > 0) {
            unsyncedVisits.forEach { visit ->
                visitDao.markAsSynced(
                    visit.id,
                    LocalDateTime.now().toString()
                )
            }
        }

        return response
    }

    suspend fun fetchFarms(): List<FarmEntity> {
        return api.getFarms().map { it.toEntity() }
    }

    suspend fun fetchProducts(): List<ProductEntity> {
        return api.getProducts(activeOnly = true).map { it.toEntity() }
    }
}
```

### 3. ViewModel Example

**`ui/screens/visit/VisitViewModel.kt`**
```kotlin
@HiltViewModel
class VisitViewModel @Inject constructor(
    private val repository: VisitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitUiState())
    val uiState: StateFlow<VisitUiState> = _uiState.asStateFlow()

    init {
        loadDropdownData()
    }

    private fun loadDropdownData() {
        viewModelScope.launch {
            try {
                val farms = repository.fetchFarms()
                val products = repository.fetchProducts()
                _uiState.update {
                    it.copy(farms = farms, products = products, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun recordVisit(farmId: String, productId: String, quantity: Double, notes: String?) {
        viewModelScope.launch {
            val visit = VisitEntity(
                id = UUID.randomUUID().toString(),
                salesmanId = "current-salesman-id", // From auth state
                farmId = farmId,
                productId = productId,
                quantity = quantity,
                visitDate = LocalDateTime.now().toString(),
                notes = notes,
                deviceId = "device-id", // From device
                createdAt = LocalDateTime.now().toString(),
                syncedAt = null,
                isSynced = false
            )

            repository.recordVisit(visit)
            _uiState.update { it.copy(visitRecorded = true) }
        }
    }
}

data class VisitUiState(
    val farms: List<FarmEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val visitRecorded: Boolean = false
)
```

### 4. Jetpack Compose Screen

**`ui/screens/visit/VisitScreen.kt`**
```kotlin
@Composable
fun VisitRecordingScreen(
    viewModel: VisitViewModel = hiltViewModel(),
    onVisitRecorded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.visitRecorded) {
        if (uiState.visitRecorded) {
            onVisitRecorded()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Record Visit", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Farm dropdown
        DropdownMenu(
            label = "Select Farm",
            items = uiState.farms,
            onItemSelected = { /* handle */ }
        )

        // Product dropdown
        DropdownMenu(
            label = "Select Product",
            items = uiState.products,
            onItemSelected = { /* handle */ }
        )

        // Quantity input
        OutlinedTextField(
            value = "",
            onValueChange = { /* handle */ },
            label = { Text("Quantity (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Notes input
        OutlinedTextField(
            value = "",
            onValueChange = { /* handle */ },
            label = { Text("Notes (optional)") },
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* call viewModel.recordVisit() */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Visit")
        }
    }
}
```

### 5. WorkManager Sync

**`worker/SyncWorker.kt`**
```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: VisitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val response = repository.syncVisits()

            if (response.failed > 0) {
                Result.retry()
            } else {
                Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_visits"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}
```

## Build Instructions

### 1. Open in Android Studio

```bash
# Open Android Studio
# File → Open → Select /Users/ibhan/workshop/feedright/android-app
```

### 2. Sync Gradle

Android Studio will automatically:
- Download dependencies
- Configure the project
- Index files

This may take 5-10 minutes on first run.

### 3. Create Missing Files

You'll need to create:
- Resource files (strings.xml, colors.xml, themes.xml)
- Remaining repository classes
- All UI screens and components
- Navigation graph
- Hilt modules
- WorkManager setup

**See "Remaining Files Checklist" section below**

### 4. Update API URL (If Using Physical Device)

Edit `app/build.gradle.kts`:

```kotlin
// For emulator (default)
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080/api/\"")

// For physical device (replace with your computer's IP)
buildConfigField("String", "API_BASE_URL", "\"http://192.168.1.100:8080/api/\"")
```

### 5. Run the App

- **Emulator:** Tools → Device Manager → Create/Start emulator
- **Physical Device:** Enable USB debugging, connect via USB
- Click **Run** (green play button) or press `Shift + F10`

## Remaining Files Checklist

### Critical (App Won't Build Without)

- [ ] `app/src/main/res/values/strings.xml`
- [ ] `app/src/main/res/values/colors.xml`
- [ ] `app/src/main/res/values/themes.xml`
- [ ] `app/src/main/res/xml/data_extraction_rules.xml`
- [ ] `app/src/main/res/xml/backup_rules.xml`
- [ ] `app/proguard-rules.pro`
- [ ] `gradle.properties`

### Hilt Modules (Required)

- [ ] `di/DatabaseModule.kt`
- [ ] `di/NetworkModule.kt`
- [ ] `di/RepositoryModule.kt`

### Repositories

- [ ] `data/repository/VisitRepository.kt`
- [ ] `data/repository/FarmRepository.kt`
- [ ] `data/repository/ProductRepository.kt`
- [ ] `data/repository/AuthRepository.kt`

### ViewModels

- [ ] `ui/screens/login/LoginViewModel.kt`
- [ ] `ui/screens/home/HomeViewModel.kt`
- [ ] `ui/screens/visit/VisitViewModel.kt`

### UI Screens

- [ ] `ui/screens/login/LoginScreen.kt`
- [ ] `ui/screens/home/HomeScreen.kt`
- [ ] `ui/screens/visit/VisitRecordingScreen.kt`
- [ ] `ui/navigation/NavGraph.kt`
- [ ] `ui/theme/Theme.kt`
- [ ] `ui/theme/Color.kt`
- [ ] `ui/theme/Type.kt`

### Components

- [ ] `ui/components/DropdownField.kt`
- [ ] `ui/components/LoadingIndicator.kt`
- [ ] `ui/components/ErrorMessage.kt`

### Worker

- [ ] `worker/SyncWorker.kt`

### DAOs

- [ ] `data/local/dao/FarmDao.kt`
- [ ] `data/local/dao/ProductDao.kt`

## Features Roadmap

### Phase 1: MVP (Current)
- [x] Project structure created
- [x] Gradle configuration
- [x] Room database entities
- [x] Retrofit API interface
- [ ] Complete Hilt setup
- [ ] Build all UI screens
- [ ] Implement offline sync
- [ ] Test end-to-end

### Phase 2: Enhancement
- [ ] Add image capture for visit photos
- [ ] Implement search/filter in visit history
- [ ] Add visit editing
- [ ] Improve error handling
- [ ] Add unit tests

### Phase 3: Polish
- [ ] Add animations
- [ ] Implement dark mode
- [ ] Add accessibility features
- [ ] Performance optimization
- [ ] Add analytics

## Testing Strategy

### Unit Tests
```kotlin
// Example: VisitRepositoryTest.kt
@Test
fun `recordVisit saves to database`() = runTest {
    val visit = VisitEntity(...)
    repository.recordVisit(visit)
    val saved = visitDao.getRecentVisits().first()
    assertEquals(visit.id, saved.first().id)
}
```

### UI Tests
```kotlin
// Example: VisitScreenTest.kt
@Test
fun `visit recording flow completes successfully`() {
    composeTestRule.setContent {
        VisitRecordingScreen(...)
    }
    composeTestRule.onNodeWithText("Select Farm").performClick()
    // ... test UI interactions
}
```

## Troubleshooting

### Build Errors

**Error:** "Missing strings.xml"
**Fix:** Create `app/src/main/res/values/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">FeedRight</string>
</resources>
```

**Error:** "Cannot resolve symbol Hilt"
**Fix:** Sync Gradle files: File → Sync Project with Gradle Files

**Error:** "API_BASE_URL not found"
**Fix:** Rebuild project: Build → Rebuild Project

### Runtime Errors

**Error:** "Unable to connect to backend"
**Fix:**
1. Check backend is running on port 8080
2. For emulator, use `10.0.2.2:8080`
3. For device, use your computer's IP address

**Error:** "Database migration failed"
**Fix:** Uninstall app and reinstall (dev only)

## Performance Considerations

- **Room:** Queries run on background thread (IO dispatcher)
- **Retrofit:** Suspending functions (no blocking)
- **Compose:** Recomposition optimized with `remember` and `derivedStateOf`
- **WorkManager:** Battery-efficient periodic sync

## Security Notes

- **API:** Using cleartext traffic for development (disable in production)
- **Storage:** Room database not encrypted (consider SQLCipher for production)
- **PIN:** Currently hardcoded (implement proper auth flow)

## Next Steps

1. **Complete the remaining files** (see checklist above)
2. **Build and run** in Android Studio
3. **Test offline functionality** by toggling airplane mode
4. **Test sync** by creating visits offline, then going online
5. **Deploy** to internal test track for pilot users

---

**Status:** 🚧 Project structure created, awaiting completion in Android Studio

**Estimated completion time:** 2-3 days for experienced Android developer

**Files created:** 9 core files
**Files remaining:** ~30 files (see checklist)

For questions or issues, refer to the main project README at `/Users/ibhan/workshop/feedright/README.md`
