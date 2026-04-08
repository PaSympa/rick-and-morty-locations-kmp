package fr.leandremru.rickandmortylocations.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailAction
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

/**
 * Type-safe navigation destinations for the mobile flow.
 *
 * Each destination is a [NavKey] consumed by Nav3's [NavDisplay]. The Desktop
 * layout intentionally does NOT use this graph: it shows list + detail on a
 * single screen via [fr.leandremru.rickandmortylocations.presentation.screens.desktop.LocationsDesktopScreen].
 */
sealed interface Destination : NavKey {

    /** Locations list — start destination on mobile. */
    @Serializable
    data object LocationList : Destination

    /** Detail of a single location, identified by its [locationId]. */
    @Serializable
    data class LocationDetail(val locationId: Int) : Destination
}

/**
 * Centralized navigator for the mobile flow.
 *
 * This `object` is the single source of truth for navigation requests across the
 * application. Any composable that needs to navigate calls [navigate] / [back]
 * instead of holding a reference to the back stack — keeping screens free of any
 * navigation plumbing and making the navigation surface auditable from one place.
 *
 * Events are emitted through a hot [SharedFlow] consumed by [AppNavHost], which is
 * the only collector and the only owner of the actual back stack state.
 */
object AppNavigator {

    private val _events = MutableSharedFlow<NavEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    /** Push the given [destination] onto the back stack. */
    fun navigate(destination: Destination) {
        _events.tryEmit(NavEvent.GoTo(destination))
    }

    /** Pop the current entry from the back stack. */
    fun back() {
        _events.tryEmit(NavEvent.Back)
    }
}

/** One-shot navigation events emitted by [AppNavigator] and consumed by [AppNavHost]. */
sealed interface NavEvent {
    data class GoTo(val destination: Destination) : NavEvent
    data object Back : NavEvent
}

/**
 * Mobile navigation host — composition root for the Nav3 graph.
 *
 * Owns the back stack, collects every navigation request emitted by [AppNavigator],
 * and resolves each entry's ViewModel via Koin through small private `*Entry`
 * composables. Screens themselves stay agnostic about Koin and navigation
 * plumbing: they only receive a `state` and an `onAction` callback.
 */
@Composable
fun AppNavHost() {
    val backStack = remember { mutableStateListOf<NavKey>(Destination.LocationList) }

    // Single subscription to AppNavigator: every navigation request from the app
    // funnels through this collector, which is the only place that mutates the back stack.
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
        entryProvider = { key ->
            NavEntry(key) {
                when (key) {
                    Destination.LocationList -> LocationListEntry()
                    is Destination.LocationDetail -> LocationDetailEntry(locationId = key.locationId)
                }
            }
        },
    )
}

/** Composition root for the locations list entry: resolves the VM, observes state. */
@Composable
private fun LocationListEntry() {
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

/** Composition root for the detail entry: resolves the VM, observes state, dispatches `Load`. */
@Composable
private fun LocationDetailEntry(locationId: Int) {
    val viewModel = koinViewModel<LocationDetailViewModel>()
    LaunchedEffect(locationId) {
        viewModel.onAction(LocationDetailAction.Load(locationId))
    }
    val state by viewModel.state.collectAsState()
    LocationDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = { AppNavigator.back() },
    )
}
