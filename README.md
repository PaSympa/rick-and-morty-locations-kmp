# Rick & Morty Locations — KMP

A Kotlin Multiplatform application targeting **Android** and **Desktop**, built around the [Rick and Morty API](https://rickandmortyapi.com) — focused on the **Locations** endpoint.

The project is a demonstration of Clean Architecture, MVI / Unidirectional Data Flow, type-safe navigation and KMP cross-native specialization, all sharing a single `composeApp` module.

---

## Architecture overview

The codebase follows a strict **Clean Architecture** layering. Dependencies always point inward: the `domain` layer is at the center and knows nothing about Compose, Ktor or SQLDelight.

```
┌────────────────────────────────────────────────────────────┐
│                       Presentation                         │
│  Compose screens · MVI Stores · Navigator · Design system  │
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
│  Ktor remote source · SQLDelight local source · mappers    │
└────────────────────────────────────────────────────────────┘

                  cross-cutting (used by all)
┌────────────────────────────────────────────────────────────┐
│       Core: MVI base · Koin DI · AudioManager (KMP)        │
└────────────────────────────────────────────────────────────┘
```

| Layer | Responsibility | Knows about |
|---|---|---|
| **Presentation** | Compose UI, MVI `Store` / `UiState` / `UiAction`, navigation, design system | Domain only |
| **Domain** | Business models and contracts (`Location`, `LocationRepository`) | Nothing — pure Kotlin |
| **Data** | Remote (Ktor) and local (SQLDelight) sources, DTOs, Entities, mappers, `LocationRepositoryImpl` | Domain only (implements its contracts) |
| **Core** | Cross-cutting infrastructure: MVI base classes, Koin modules, `AudioManager` (`expect`/`actual`) | Domain |

---

## Project structure

```
composeApp/src/commonMain/kotlin/fr/leandremru/rickandmortylocations/
├── App.kt                      # root composable (Nav3 host)
├── Platform.kt                 # expect fun getPlatform()
│
├── core/
│   ├── audio/                  # AudioManager (expect/actual KMP)
│   ├── di/                     # Koin modules + initKoin()
│   └── presentation/           # Store / StoreViewModel / StoreAction (MVI base)
│
├── domain/
│   ├── model/                  # Location (pure data class)
│   └── repository/             # LocationRepository interface
│
├── data/
│   ├── local/
│   │   ├── dao/                # LocationDao wrapper around SQLDelight queries
│   │   ├── db/                 # DatabaseDriverFactory (expect/actual)
│   │   └── mapper/             # Entity ↔ Domain mappers
│   ├── remote/
│   │   ├── api/                # LocationApi (Ktor service)
│   │   ├── dto/                # @Serializable DTOs
│   │   └── mapper/             # DTO → Domain mapper
│   └── repository/             # LocationRepositoryImpl (cache-then-network)
│
└── presentation/
    ├── components/             # Reusable RnM design system (RnMButton, RnMCard, ...)
    ├── navigation/             # Routes, AppNavigator (object), AppNavHost
    ├── theme/                  # RnMTheme, colors, typography
    └── screens/
        ├── locationlist/       # Mobile list screen (MVI)
        ├── locationdetail/     # Mobile detail screen (MVI)
        └── desktop/            # Desktop master-detail (single screen)
```

Platform-specific actuals live in mirrored package paths under `androidMain/` and `jvmMain/` (e.g. `data/local/db/DatabaseDriverFactory.android.kt`).

---

## Tech stack

| Concern | Library | Version |
|---|---|---|
| UI | Compose Multiplatform + Material 3 | 1.10.3 / 1.10.0-alpha05 |
| Navigation | [Navigation 3](https://developer.android.com/jetpack/androidx/releases/navigation) (JetBrains fork) | 1.1.0-beta01 |
| HTTP client | [Ktor](https://ktor.io) | 3.4.2 |
| JSON | Kotlinx Serialization | 1.10.0 |
| Local DB | [SQLDelight](https://cashapp.github.io/sqldelight/) | 2.3.2 |
| DI | [Koin](https://insert-koin.io) | 4.2.0 |
| Coroutines | kotlinx.coroutines | 1.10.2 |
| Kotlin / AGP | Kotlin 2.3.20 / AGP 8.13.2 | — |

> AGP is intentionally pinned at `8.13.2`. AGP 9.x is not yet compatible with the Kotlin Multiplatform plugin in a single-module setup ([context](https://kotl.in/kmp-project-structure-migration)).

---

## MVI / Unidirectional Data Flow

Each screen is built around three first-class concepts that live in dedicated files:

- **`UiState`** — an immutable data class describing everything the screen needs to render.
- **`UiAction`** — a sealed hierarchy of intents the user (or the system) can dispatch.
- **`Store`** — owns the `StateFlow<UiState>`, applies actions through a `reduce()` extension, runs side effects (data fetches, navigation events).

The `ViewModel` is a thin wrapper around the `Store`. The composable screen only observes state and forwards actions — it contains zero business logic. Base classes (`Store`, `StoreViewModel`, `StoreAction`) live in `core/presentation/` and are reused across all screens.

---

## Cross-Native (KMP `expect` / `actual`)

Two cross-native concerns are implemented through the canonical `expect` / `actual` pattern, each placed in its semantic layer rather than in a separate folder:

- **`data/local/db/DatabaseDriverFactory`** — provides the SQLDelight `SqlDriver`. Android uses `AndroidSqliteDriver`; Desktop uses `JdbcSqliteDriver`.
- **`core/audio/AudioManager`** — plays a short feedback sound when the user opens a location detail. Android uses `MediaPlayer` (constructed via a `Context` extension function); Desktop uses `javax.sound.sampled`.

A third existing example, `Platform.kt` at the package root, is the wizard's `expect fun getPlatform()` and is kept for completeness.

---

## How to run

### Android

From the IDE: select the `composeApp` Android run configuration and click ▶︎.

From the terminal:
```shell
./gradlew :composeApp:installDebug
```
This builds the debug APK and installs it on the connected device or emulator.

### Desktop (JVM)

From the IDE: select the `composeApp [desktop]` run configuration and click ▶︎.

From the terminal:
```shell
./gradlew :composeApp:run
```
This launches the Compose Multiplatform desktop window.

---

## Why these choices

| Decision | Reason |
|---|---|
| Single `composeApp` module | Simpler Gradle, matches the JetBrains template, avoids premature modularization. |
| Navigation 3 over Voyager / Decompose | Type-safe `@Serializable` routes + centralized `object Navigator`, latest official JetBrains stack. |
| SQLDelight over Room | More KMP-native; Room on Desktop is still alpha. SQLDelight gives a real DAO/Entity pair generated from `.sq` files. |
| Koin over Hilt | KMP-first, single API for `commonMain`/`androidMain`/`jvmMain`, supports `expect/actual` platform modules cleanly. |
| Cache-then-network in `LocationRepositoryImpl` | Demonstrates a real fetch strategy across local and remote sources, satisfies the Clean Architecture data flow expectations. |
| Dispersed `expect`/`actual` (no `cross/` folder) | Each cross-native concern lives in the layer it serves; consistent with the canonical KMP convention. |

---

## License

Educational project, not for production use.
