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

/**
 * ViewModel for the locations list screen.
 *
 * Follows a UDF / MVI loop:
 *  1. State is exposed as an immutable [StateFlow].
 *  2. The screen reacts by dispatching a [LocationListAction] through [onAction].
 *  3. Effects (repository calls) run on [viewModelScope] and feed the state back via [update].
 */
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
        // Repository exposes a Flow: the first emission comes from the Room cache (or
        // from the network on a cold start), and any later upsert refreshes the UI.
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
