package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.leandremru.rickandmortylocations.core.audio.AudioManager
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailUiState.Phase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UDF ViewModel for the location detail screen.
 *
 * The id is dispatched as an action ([LocationDetailAction.Load]) rather than
 * passed via the constructor: this lets the same instance serve consecutive
 * selections on Desktop master-detail without being recreated.
 */
class LocationDetailViewModel(
    private val repository: LocationRepository,
    private val audioManager: AudioManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LocationDetailUiState())
    val state: StateFlow<LocationDetailUiState> = _state.asStateFlow()

    fun onAction(action: LocationDetailAction) {
        when (action) {
            is LocationDetailAction.Load -> if (_state.value.requestedId != action.id) load(action.id)
            LocationDetailAction.Retry -> _state.value.requestedId?.let(::load)
        }
    }

    private fun load(id: Int) {
        // Cross-native side effect: opening a location plays the portal sound.
        audioManager.playPortalClick()
        _state.update {
            it.copy(phase = Phase.Loading, requestedId = id, location = null, errorMessage = null)
        }
        viewModelScope.launch {
            runCatching { repository.getLocationById(id) }
                .onSuccess { location ->
                    _state.update { it.copy(phase = Phase.Loaded, location = location) }
                }
                .onFailure { error ->
                    _state.update { it.copy(phase = Phase.Error, errorMessage = error.message) }
                }
        }
    }
}
