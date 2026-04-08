package fr.leandremru.rickandmortylocations.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

/**
 * Root navigation host. Wires [AppNavigator]'s event stream to a Nav3 back stack
 * and maps each [AppRoute] to its concrete screen via the entry provider.
 */
@Composable
fun AppNavHost() {
    val backStack = remember { mutableStateListOf<NavKey>(LocationListRoute) }

    LaunchedEffect(Unit) {
        AppNavigator.events.collect { event ->
            when (event) {
                is NavEvent.GoTo -> backStack.add(event.route)
                NavEvent.Back -> if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { AppNavigator.navigateBack() },
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = { key ->
            NavEntry(key) {
                when (key) {
                    is LocationListRoute -> Placeholder("LocationListScreen — coming next phase")
                    is LocationDetailRoute -> Placeholder("LocationDetailScreen — id=${key.locationId}")
                    else -> Placeholder("Unknown route: $key")
                }
            }
        },
    )
}

@Composable
private fun Placeholder(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
