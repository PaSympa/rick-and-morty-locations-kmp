package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.domain.model.Location
import fr.leandremru.rickandmortylocations.presentation.components.RnMErrorState
import fr.leandremru.rickandmortylocations.presentation.components.RnMLocationCard
import fr.leandremru.rickandmortylocations.presentation.components.RnMPortalLoader

/** Stateless locations list screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    state: LocationListUiState,
    onAction: (LocationListAction) -> Unit,
    onLocationSelected: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "RICK & MORTY · LOCATIONS",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (state.phase) {
                LocationListUiState.Phase.Loading -> RnMPortalLoader(message = "Scanning the multiverse...")
                LocationListUiState.Phase.Loaded -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items = state.locations, key = { it.id }) { location ->
                        RnMLocationCard(
                            location = location,
                            onClick = { onLocationSelected(location) },
                        )
                    }
                }
                LocationListUiState.Phase.Error -> RnMErrorState(
                    message = state.errorMessage ?: "Couldn't reach the dimension server.",
                    onRetry = { onAction(LocationListAction.Retry) },
                )
            }
        }
    }
}
