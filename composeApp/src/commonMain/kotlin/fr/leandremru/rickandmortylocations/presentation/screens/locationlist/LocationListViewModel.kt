package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListUiState.Phase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/** UDF ViewModel for the locations list screen. */
class LocationListViewModel(
    private val repository: LocationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LocationListUiState())
    val state: StateFlow<LocationListUiState> = _state.asStateFlow()

    init {
        onAction(LocationListAction.Load)
    }

    fun onAction(action: LocationListAction) {
        when (action) {
            LocationListAction.Load,
            LocationListAction.Retry -> loadLocations()
        }
    }

    private fun loadLocations() {
        _state.update { it.copy(phase = Phase.Loading, errorMessage = null) }
        // Repository exposes a Flow: the first emission is the Room cache (or
        // a fresh fetch if empty), and any later upsert refreshes the UI.
        repository.getLocations()
            .onEach { locations ->
                _state.update { it.copy(phase = Phase.Loaded, locations = locations) }
            }
            .catch { error ->
                _state.update { it.copy(phase = Phase.Error, errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }
}
