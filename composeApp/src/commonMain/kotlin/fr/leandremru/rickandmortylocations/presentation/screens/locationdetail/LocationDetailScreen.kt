package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.domain.model.Location
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.actions.LoadLocationDetail
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.actions.NavigateBack
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Public entry point for the location detail screen.
 * Resolves a [LocationDetailViewModel] scoped to [locationId] from Koin so each
 * navigated detail gets its own store with the right id.
 */
@Composable
fun LocationDetailScreen(locationId: Int) {
    val viewModel = koinViewModel<LocationDetailViewModel>(
        parameters = { parametersOf(locationId) },
    )
    val state by viewModel.state.collectAsState()
    LocationDetailContent(state = state, onAction = viewModel::handleAction)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailContent(
    state: LocationDetailUiState,
    onAction: (LocationDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.location?.name ?: "Location") },
                navigationIcon = {
                    TextButton(onClick = { onAction(NavigateBack) }) { Text("←") }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            when (state.phase) {
                LocationDetailUiState.Phase.Loading -> CircularProgressIndicator()
                LocationDetailUiState.Phase.Loaded -> state.location?.let { LoadedDetail(it) }
                LocationDetailUiState.Phase.Error -> ErrorState(
                    message = state.errorMessage ?: "Unknown error",
                    onRetry = { onAction(LoadLocationDetail) },
                )
            }
        }
    }
}

@Composable
private fun LoadedDetail(location: Location) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = location.name, style = MaterialTheme.typography.headlineMedium)
        HorizontalDivider()
        DetailRow(label = "Type", value = location.type)
        DetailRow(label = "Dimension", value = location.dimension)
        DetailRow(label = "Residents", value = "${location.residentCount}")
        if (location.residentIds.isNotEmpty()) {
            Text(
                text = "Resident IDs: ${location.residentIds.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        location.createdAt?.let { DetailRow(label = "Created", value = it) }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(24.dp),
    ) {
        Text(text = message)
        Button(onClick = onRetry) { Text("Retry") }
    }
}
