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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.presentation.navigation.Destination
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListScreen
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Desktop master-detail composition root.
 *
 * Reuses the stateless mobile screens. Selection is held as local Compose state,
 * not navigation: clicking on the left pane updates `selectedLocationId` and the
 * right pane resolves a fresh `LocationDetailViewModel` keyed by that id.
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
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "🌀",
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    text = "Pick a location",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Select a location on the left to open its portal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
        return
    }

    val viewModel = koinViewModel<LocationDetailViewModel>(
        key = "detail-$selectedLocationId",
    ) {
        parametersOf(Destination.LocationDetail(selectedLocationId))
    }
    val state by viewModel.state.collectAsState()
    LocationDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        onNavigateBack = null,
    )
}
