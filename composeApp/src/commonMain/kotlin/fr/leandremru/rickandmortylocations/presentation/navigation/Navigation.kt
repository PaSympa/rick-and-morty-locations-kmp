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

/** Type-safe navigation destinations for the mobile flow. */
sealed interface Destination : NavKey {
    @Serializable data object LocationList : Destination
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
    val events: SharedFlow<NavEvent> = _events.asSharedFlow()

    fun navigate(destination: Destination) {
        _events.tryEmit(NavEvent.GoTo(destination))
    }

    fun back() {
        _events.tryEmit(NavEvent.Back)
    }
}

sealed interface NavEvent {
    data class GoTo(val destination: Destination) : NavEvent
    data object Back : NavEvent
}

/**
 * Mobile navigation host — composition root for the Nav3 graph.
 *
 * Owns the back stack, collects [AppNavigator] events, and resolves each
 * entry's ViewModel via Koin in the small private `*Entry` composables —
 * the screens themselves never import Koin.
 */
@Composable
fun AppNavHost() {
    val backStack = remember { mutableStateListOf<NavKey>(Destination.LocationList) }

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
