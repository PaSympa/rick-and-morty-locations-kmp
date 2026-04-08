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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailAction
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Desktop master-detail composition root.
 *
 * Reuses the stateless mobile screens. Selection is local Compose state, not
 * navigation: clicking on the left pane updates `selectedLocationId` and the
 * right pane reacts by dispatching a fresh `Load` action to the same VM
 * instance — no recreation between selections.
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
    LocationListScreen(
        state = state,
        onAction = viewModel::onAction,
        onLocationSelected = { onLocationSelected(it.id) },
        modifier = modifier,
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

    val viewModel = koinViewModel<LocationDetailViewModel>()
    LaunchedEffect(selectedLocationId) {
        viewModel.onAction(LocationDetailAction.Load(selectedLocationId))
    }
    val state by viewModel.state.collectAsState()
    LocationDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        onNavigateBack = null,
    )
}
