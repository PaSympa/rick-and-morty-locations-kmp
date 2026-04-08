# Rick & Morty Locations — KMP

A Kotlin Multiplatform application targeting **Android** and **Desktop (JVM)**, built around the [Rick and Morty API](https://rickandmortyapi.com) — focused on the **Locations** endpoint.

The project demonstrates a clean separation between **Domain / Data / Presentation**, a **UDF / MVI** presentation loop, a real **two-source data layer** (Room + Ktor) with a Flow-based fetch strategy, and the canonical **`expect / actual`** mechanism for cross-native code.

Everything ships from a single `composeApp` Gradle module.

---

## Architecture overview

The codebase follows a clean architecture layering. Dependencies always point inward: the **Domain** is at the center and knows nothing about Compose, Ktor or Room.

```
┌────────────────────────────────────────────────────────────┐
│                       Presentation                         │
│   Compose screens · ViewModels (UDF) · Navigation · UI kit │
└────────────────────────┬───────────────────────────────────┘
                         │ depends on
                         ▼
┌────────────────────────────────────────────────────────────┐
│                          Domain                            │
│        Pure Kotlin models · Repository interfaces          │
└────────────────────────▲───────────────────────────────────┘
                         │ implemented by
┌────────────────────────┴───────────────────────────────────┐
│                            Data                            │
│   Ktor remote source · Room local source · mappers · repo  │
└────────────────────────────────────────────────────────────┘

                       cross-cutting
┌────────────────────────────────────────────────────────────┐
│  core/  →  Koin DI · AudioManager (expect/actual KMP)      │
└────────────────────────────────────────────────────────────┘
```

| Layer | Responsibility | Knows about |
|---|---|---|
| **Presentation** | Compose UI, ViewModels, sealed `Action` / `UiState`, navigation, reusable components | Domain only |
| **Domain** | Business models and contracts (`Location`, `LocationRepository`) | Nothing — pure Kotlin |
| **Data** | `LocationApi` (Ktor), `LocationDao` + `LocationsDatabase` (Room), DTOs / Entities, mappers, `LocationRepositoryImpl` | Domain only (implements its contracts) |
| **Core** | Cross-cutting infrastructure: Koin modules, `AudioManager` (`expect`/`actual`) | Domain |

---

## Project structure

```
composeApp/src/commonMain/kotlin/fr/leandremru/rickandmortylocations/
├── App.kt                      # Root composable, wraps the mobile NavHost in MaterialTheme
│
├── core/
│   ├── audio/AudioManager.kt   # expect class AudioManager (KMP)
│   └── di/
│       ├── InitKoin.kt         # initKoin() — single entry point per platform
│       ├── Modules.kt          # remoteModule + databaseModule + repositoryModule + viewModelModule + sharedModules()
│       └── PlatformModules.kt  # expect fun platformModules(): List<Module>
│
├── domain/
│   ├── model/Location.kt                       # pure data class
│   └── repository/LocationRepository.kt        # contract: Flow<List<Location>> + suspend getById
│
├── data/
│   ├── local/
│   │   ├── dao/LocationDao.kt                  # Room DAO with observeAll() : Flow
│   │   ├── db/LocationsDatabase.kt             # Room @Database + expect object constructor
│   │   ├── db/LocationEntity.kt                # @Entity
│   │   └── Mappers.kt                          # Entity ↔ Domain  +  Dto → Entity
│   ├── remote/
│   │   ├── api/LocationApi.kt                  # Ktor service for /location
│   │   ├── dto/LocationDto.kt                  # @Serializable wire model
│   │   ├── dto/InfoDto.kt
│   │   ├── dto/PaginatedDto.kt
│   │   └── HttpClientFactory.kt
│   └── repository/LocationRepositoryImpl.kt    # Cache-then-network, Room as source of truth
│
└── presentation/
    ├── components/                             # RnMLocationCard / RnMLabeledRow / RnMErrorState
    ├── navigation/Navigation.kt                # sealed Destination + AppNavHost (composition root, mobile)
    └── screens/
        ├── locationlist/                       # UiState / Action / ViewModel / stateless Screen
        ├── locationdetail/                     # Same triplet
        └── desktop/LocationsDesktopScreen.kt   # Desktop master-detail (composition root, desktop)
```

Platform-specific actuals live under mirrored package paths:

```
androidMain/.../core/audio/AudioManager.android.kt          # actual class AudioManager(context)
androidMain/.../core/audio/ContextExtensions.kt             # fun Context.createAudioManager(): AudioManager
androidMain/.../core/di/PlatformModules.android.kt          # Room builder + audio binding
androidMain/.../RickAndMortyApp.kt                          # Application entry, calls initKoin()
androidMain/.../MainActivity.kt

jvmMain/.../core/audio/AudioManager.jvm.kt                  # actual class AudioManager()  (javax.sound.sampled)
jvmMain/.../core/di/PlatformModules.jvm.kt                  # Room builder + audio binding
jvmMain/.../main.kt                                         # Desktop entry, calls initKoin() then opens window
```

---

## Tech stack

| Concern | Library | Version |
|---|---|---|
| UI | Compose Multiplatform + Material 3 | 1.10.3 / 1.10.0-alpha05 |
| Navigation | Navigation 3 (JetBrains fork) | 1.1.0-beta01 |
| HTTP client | Ktor | 3.4.2 |
| JSON | kotlinx.serialization | 1.10.0 |
| Local DB | Room (KMP) + bundled SQLite | 2.8.4 / 2.6.2 |
| DI | Koin | 4.2.0 |
| Coroutines | kotlinx.coroutines | 1.10.2 |
| Kotlin / AGP | Kotlin 2.3.20 / AGP 8.13.2 | — |

---

## Presentation layer — UDF / MVI

Each screen is built around three first-class concepts that live in dedicated files:

- **`UiState`** — an immutable `data class` describing everything the screen needs to render. Includes a `Phase` enum (`Loading` / `Loaded` / `Error`).
- **`Action`** — a `sealed interface` listing every user-driven event the ViewModel reacts to (e.g. `Load`, `Retry`, `Load(id)`).
- **`ViewModel`** — a plain `androidx.lifecycle.ViewModel` exposing `state: StateFlow<UiState>` and a single `onAction(action: Action)` entry point. Side effects run on `viewModelScope`.

The composables themselves are **stateless** : they take `state` + `onAction` + a navigation callback and contain zero business logic. Koin is **never imported in a screen file** — VMs are resolved only at the composition roots:

- `presentation/navigation/Navigation.kt → AppNavHost()` for mobile
- `presentation/screens/desktop/LocationsDesktopScreen.kt` for desktop

Selection (clicking a location) is intentionally **not** modeled as a ViewModel action: it is a UI concern and is handled by a screen-level callback. This makes the same `LocationListScreen` reusable inside the Desktop master-detail without involving navigation.

---

## Data layer — two sources, one fetch strategy

The data layer wires two sources around the same `Location` aggregate:

| Source | Library | Role |
|---|---|---|
| Remote | Ktor (`LocationApi`) | Fetches the first page of locations and the detail of a single location from `https://rickandmortyapi.com/api/location`. |
| Local | Room (`LocationDao`, `LocationsDatabase`) | Cache + source of truth observed by the UI through `observeAll(): Flow<List<LocationEntity>>`. |

The fetch strategy lives in `LocationRepositoryImpl.getLocations()`:

1. The repository returns `dao.observeAll()` — a `Flow` backed by Room. The UI subscribes once and reacts to every cache update.
2. `onStart { refreshIfEmpty() }` runs in parallel: if the local table is empty, the API is called and the results are upserted into Room. Because the Flow observes the same table, this immediately triggers a fresh emission downstream.
3. Mapping `Entity → Domain` is done in the final `map { ... }` operator, keeping each step (read / fetch / map) independently readable.

The single-detail path stays `suspend` since there is nothing to observe: serve from cache when available, otherwise hit the API and persist for later subscribers.

The Room database is built from a **platform-specific `RoomDatabase.Builder`** provided by `platformModules()`. The KMP `expect`/`actual` boundary itself is in `LocationsDatabaseConstructor`, whose actuals are generated automatically by the Room compiler.

---

## Cross-Native (`expect` / `actual`)

Two cross-native concerns demonstrate the canonical pattern:

### `core/audio/AudioManager` — feedback sound

Declared as `expect class AudioManager` in `commonMain`:

```kotlin
expect class AudioManager {
    fun playPortalClick()
    fun playThemeSong()
}
```

- **Android** (`AudioManager.android.kt`) — `actual class AudioManager(private val context: Context)` backed by `MediaPlayer`. Resources are looked up at runtime via `Resources.getIdentifier(...)` so a missing audio file becomes a silent no-op rather than a crash.
- **Desktop** (`AudioManager.jvm.kt`) — `actual class AudioManager()` backed by `javax.sound.sampled.Clip`. Audio files are loaded from the JVM classpath.

The Android instance is built via a **dedicated `Context` extension function** (`fun Context.createAudioManager(): AudioManager`), which is then wired in Koin as a single readable line:

```kotlin
single { androidContext().createAudioManager() }
```

The portal sound is fired as a deliberate side effect inside `LocationDetailViewModel` whenever a `Load(id)` action is dispatched, tying the cross-native manager to a real user interaction (opening a location detail) rather than being a decorative bonus. The theme song is fired once at launch from the platform entry points (`RickAndMortyApp.onCreate()` on Android, `main()` on Desktop).

### `data/local/db/LocationsDatabaseConstructor` — Room database constructor

Room's KMP support requires an `expect object` whose `actual` is auto-generated by KSP per target. This is the second example of `expect / actual` in the project, but it is purely structural — the interesting cross-native concern is the audio manager.

---

## Dependency Injection

Koin is organized around a single composition function `sharedModules(): List<Module>` that aggregates the four cross-platform modules:

| Module | Provides |
|---|---|
| `remoteModule` | `HttpClient` and `LocationApi` |
| `databaseModule` | `LocationsDatabase` and `LocationDao`, both built from a platform-specific `RoomDatabase.Builder` |
| `repositoryModule` | `LocationRepository` (binds the implementation behind the contract) |
| `viewModelModule` | `LocationListViewModel` and `LocationDetailViewModel` |

`platformModules()` is `expect fun ... : List<Module>` — each platform contributes the bindings that depend on native APIs (the Room builder and the audio manager).

`initKoin()` is the single entry point called once per platform:

```kotlin
fun initKoin(extraConfig: KoinAppDeclaration? = null): KoinApplication =
    startKoin {
        extraConfig?.invoke(this)
        modules(platformModules() + sharedModules())
    }
```

This makes adding a new shared module a one-line change in `Modules.kt` and a new platform binding a one-line change in the relevant `PlatformModules.<target>.kt`.

---

## Mobile vs Desktop

| Aspect | Mobile (Android) | Desktop (JVM) |
|---|---|---|
| Layout | Two screens, navigation between them | Single screen, list on the left, detail on the right |
| Composition root | `AppNavHost()` in `Navigation.kt` | `LocationsDesktopScreen()` |
| Selection mechanism | Push a `Destination.LocationDetail(id)` onto the back stack | Update local Compose state (`selectedLocationId`), the right pane reacts |
| Detail VM lifecycle | One instance per navigation entry (Nav3 view model store) | One instance for the lifetime of the desktop window — same VM handles every selection |

The detail ViewModel never takes its `id` as a constructor parameter: it is dispatched as a `LocationDetailAction.Load(id)` action whenever the requested id changes. This is what enables the same VM instance to be reused on Desktop master-detail without recreation.

---

## How to run

### Android

```shell
./gradlew :composeApp:installDebug
```

Builds the debug APK and installs it on the connected device or emulator.

### Desktop (JVM)

```shell
./gradlew :composeApp:run
```

Launches the Compose Multiplatform desktop window.

---

## Why these choices

| Decision | Reason |
|---|---|
| Single `composeApp` module | Simpler Gradle, matches the JetBrains template, avoids premature modularization for a focused exercise. |
| Room over SQLDelight | Room now ships official KMP support, has a `Flow`-friendly DAO API out of the box, and works on both Android and Desktop with the bundled SQLite driver. |
| Navigation 3 | Type-safe `@Serializable` destinations + the official Jetpack Compose direction. The back stack is owned locally by the host so screens stay free of any global navigation singleton. |
| Koin over Hilt | KMP-first, single API across `commonMain` / `androidMain` / `jvmMain`, supports `expect`/`actual` platform modules cleanly. |
| `expect class` for `AudioManager` rather than an interface | Idiomatic KMP — `expect`/`actual` is the mechanism the platform expects developers to demonstrate, and it documents the cross-native intent at the type level. |
| `Flow<List<Location>>` exposed by the repository | Lets the UI subscribe once and react to cache updates without polling, makes the two-source fetch strategy honest (Room is a real source of truth, not a one-shot cache), and matches the "show the steps clearly" criterion of the data block. |
| Selection as a screen callback, not a VM action | Navigation is a UI concern. Keeping it out of the ViewModel makes the same screen reusable on Desktop master-detail without going through the navigation graph. |
| Detail id passed via `Load(id)` action | Allows a single `LocationDetailViewModel` instance to serve every selection on Desktop, instead of being recreated every time the user clicks a different location. |

---

## License

Educational project, not for production use.
