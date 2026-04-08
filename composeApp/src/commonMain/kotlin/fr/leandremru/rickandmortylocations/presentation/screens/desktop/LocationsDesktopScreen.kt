package fr.leandremru.rickandmortylocations.presentation.screens.desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailContent
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailStore
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListContent
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.actions.SelectLocation
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Desktop master-detail layout.
 *
 * Reuses the pure [LocationListContent] and [LocationDetailContent] composables
 * from the mobile screens. Selection is local state instead of navigation:
 * clicking a location in the left pane updates `selectedLocationId`, which
 * the right pane reacts to by spinning up a fresh [LocationDetailStore].
 */
@Composable
fun LocationsDesktopScreen() {
    var selectedLocationId by remember { mutableStateOf<Int?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        ListPane(
            modifier = Modifier.weight(0.4f).fillMaxHeight(),
            onLocationSelected = { selectedLocationId = it },
        )
        VerticalDivider()
        DetailPane(
            modifier = Modifier.weight(0.6f).fillMaxHeight(),
            selectedLocationId = selectedLocationId,
        )
    }
}

@Composable
private fun ListPane(
    modifier: Modifier,
    onLocationSelected: (Int) -> Unit,
) {
    val viewModel = koinViewModel<LocationListViewModel>()
    val state by viewModel.state.collectAsState()
    LocationListContent(
        state = state,
        modifier = modifier,
        onAction = { action ->
            when (action) {
                // Intercept the selection: instead of pushing a Nav3 route,
                // update the local Desktop state so the right pane updates.
                is SelectLocation -> onLocationSelected(action.location.id)
                else -> viewModel.handleAction(action)
            }
        },
    )
}

@Composable
private fun DetailPane(
    modifier: Modifier,
    selectedLocationId: Int?,
) {
    if (selectedLocationId == null) {
        Box(modifier = modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "Select a location to see its details",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        return
    }

    // Bypass the Compose ViewModel layer here: we just need a Store scoped to the
    // current selection, with its coroutine scope torn down when the selection changes.
    val repository = koinInject<LocationRepository>()
    val store = remember(selectedLocationId) {
        LocationDetailStore(locationId = selectedLocationId, repository = repository)
    }
    DisposableEffect(store) {
        onDispose { store.storeScope.close() }
    }
    val state by store.state.collectAsState()

    LocationDetailContent(
        state = state,
        onAction = { /* no nav back on Desktop master-detail */ },
        modifier = modifier,
        showBackButton = false,
    )
}
