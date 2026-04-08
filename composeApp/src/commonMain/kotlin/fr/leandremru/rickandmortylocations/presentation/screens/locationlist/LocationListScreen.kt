package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.presentation.components.RnMErrorState
import fr.leandremru.rickandmortylocations.presentation.components.RnMLocationCard
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.actions.LoadLocations
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.actions.SelectLocation
import org.koin.compose.viewmodel.koinViewModel

/**
 * Public entry point for the locations list screen.
 * Resolves the [LocationListViewModel] from Koin and delegates rendering to
 * the pure [LocationListContent] composable so the same content can be reused
 * in the Desktop master-detail layout.
 */
@Composable
fun LocationListScreen() {
    val viewModel = koinViewModel<LocationListViewModel>()
    val state by viewModel.state.collectAsState()
    LocationListContent(state = state, onAction = viewModel::handleAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListContent(
    state: LocationListUiState,
    onAction: (LocationListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Locations") }) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (state.phase) {
                LocationListUiState.Phase.Loading -> CircularProgressIndicator()
                LocationListUiState.Phase.Loaded -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items = state.locations, key = { it.id }) { location ->
                        RnMLocationCard(
                            location = location,
                            onClick = { onAction(SelectLocation(location)) },
                        )
                    }
                }
                LocationListUiState.Phase.Error -> RnMErrorState(
                    message = state.errorMessage ?: "Unknown error",
                    onRetry = { onAction(LoadLocations) },
                )
            }
        }
    }
}
