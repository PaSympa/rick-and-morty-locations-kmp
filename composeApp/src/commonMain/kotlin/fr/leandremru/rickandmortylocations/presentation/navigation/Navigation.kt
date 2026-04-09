package fr.leandremru.rickandmortylocations.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/*
 * Navigation
 * ----------
 * Three pieces work together:
 *
 *  1. [Destination] — a sealed interface whose subtypes are the typed routes.
 *     Each subtype is @Serializable so the back stack can be written to and
 *     restored from SavedState. Route arguments (like `locationId`) live
 *     inside the destination itself.
 *
 *  2. [AppNavigator] — a single object exposing `navigate(destination)` and
 *     `back()`. It is the only navigation surface visible to the rest of the
 *     app: ViewModels and screens never touch the back stack. Requests are
 *     emitted through a hot SharedFlow and consumed by [AppNavHost], the
 *     single collector.
 *
 *  3. [AppNavHost] — the composition root. It owns the back stack via
 *     [rememberNavBackStack] (saveable through [NavBackStackConfiguration],
 *     which registers the polymorphic [Destination] subtypes against
 *     [NavKey]), applies the [AppNavigator] events to it, and resolves each
 *     entry's ViewModel via Koin inside the entry block.
 *
 * For the detail entry, the typed `key` is forwarded to
 * `koinViewModel { parametersOf(key) }` so the ViewModel receives the
 * [Destination.LocationDetail] in its constructor and triggers the load (and
 * the cross-native portal sound) once in `init`. Combined with
 * [rememberViewModelStoreNavEntryDecorator], rotation and process death
 * restore the user on the same screen and never replay the side effects.
 *
 * Desktop master-detail does not use this graph: it composes the same
 * stateless screens directly with selection held as local Compose state.
 */

/** Type-safe navigation destinations for the mobile flow. */
sealed interface Destination : NavKey {
    /** Root list screen. */
    @Serializable data object LocationList : Destination

    /**
     * Detail screen for a specific location.
     *
     * @property locationId Identifier of the location to display.
     */
    @Serializable data class LocationDetail(val locationId: Int) : Destination
}

/**
 * Centralized navigator for the mobile flow.
 *
 * Single source of truth for navigation requests: any composable calls
 * [navigate] / [back] instead of holding a reference to the back stack.
 * The only collector is [AppNavHost], which owns the back stack itself.
 */
object AppNavigator {

    private val _events = MutableSharedFlow<NavEvent>(extraBufferCapacity = 8)

    /** Hot stream of navigation requests, collected once by [AppNavHost]. */
    val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    /** Push [destination] on top of the back stack. */
    fun navigate(destination: Destination) {
        _events.tryEmit(NavEvent.GoTo(destination))
    }

    /** Pop the current entry from the back stack. */
    fun back() {
        _events.tryEmit(NavEvent.Back)
    }
}

/** A navigation request emitted by [AppNavigator] and consumed by [AppNavHost]. */
sealed interface NavEvent {
    /**
     * Push a new destination on top of the back stack.
     *
     * @property destination Target destination to display.
     */
    data class GoTo(val destination: Destination) : NavEvent

    /** Pop the topmost entry from the back stack. */
    data object Back : NavEvent
}

/**
 * SavedState configuration that registers every [Destination] subtype against the
 * polymorphic [NavKey]. Required by the cross-platform `rememberNavBackStack` overload
 * to serialize/restore the back stack across configuration changes and process death.
 */
private val NavBackStackConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Destination.LocationList::class)
            subclass(Destination.LocationDetail::class)
        }
    }
}

/**
 * Mobile navigation host — composition root for the Nav3 graph.
 *
 * Owns the back stack (saveable across configuration changes and process death),
 * collects [AppNavigator] events, and resolves each entry's ViewModel via Koin
 * inside the entry block. Screens themselves never import Koin.
 */
@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(NavBackStackConfiguration, Destination.LocationList)

    LaunchedEffect(Unit) {
        AppNavigator.events.collect { event ->
            when (event) {
                is NavEvent.GoTo -> backStack.add(event.destination)
                NavEvent.Back -> if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { AppNavigator.back() },
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Destination.LocationList> {
                val viewModel = koinViewModel<LocationListViewModel>()
                val state by viewModel.state.collectAsState()
                LocationListScreen(
                    state = state,
                    onAction = viewModel::onAction,
                    onLocationSelected = { location ->
                        AppNavigator.navigate(Destination.LocationDetail(location.id))
                    },
                )
            }
            entry<Destination.LocationDetail> { key ->
                val viewModel = koinViewModel<LocationDetailViewModel> { parametersOf(key) }
                val state by viewModel.state.collectAsState()
                LocationDetailScreen(
                    state = state,
                    onAction = viewModel::onAction,
                    onNavigateBack = { AppNavigator.back() },
                )
            }
        },
    )
}
