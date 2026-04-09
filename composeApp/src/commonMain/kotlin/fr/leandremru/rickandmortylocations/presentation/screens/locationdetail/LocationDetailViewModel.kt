package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.leandremru.rickandmortylocations.core.audio.AudioManager
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.navigation.Destination
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailUiState.Phase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UDF ViewModel for the location detail screen.
 *
 * Reçoit la [Destination.LocationDetail] via `parametersOf` (Nav3) : l'id fait
 * partie de la back stack, survit aux changements de config, et le `load()`
 * d'`init` ne se rejoue pas. Déclenche aussi le son "portail" cross-native via
 * [AudioManager] à chaque ouverture / retry.
 *
 * @property navKey       Clé de navigation portant l'id à charger.
 * @property repository   Source de la location demandée.
 * @property audioManager Effet sonore "portail" déclenché à l'ouverture.
 */
class LocationDetailViewModel(
    private val navKey: Destination.LocationDetail,
    private val repository: LocationRepository,
    private val audioManager: AudioManager,
) : ViewModel() {

    private val _state = MutableStateFlow(LocationDetailUiState())

    /** État courant observé par l'écran. */
    val state: StateFlow<LocationDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    /** Point d'entrée unique des intents UI. */
    fun onAction(action: LocationDetailAction) {
        when (action) {
            LocationDetailAction.Retry -> load()
        }
    }

    private fun load() {
        // Cross-native side effect: opening a location plays the portal sound.
        audioManager.playPortalClick()
        _state.update { it.copy(phase = Phase.Loading, location = null, errorMessage = null) }
        viewModelScope.launch {
            runCatching { repository.getLocationById(navKey.locationId) }
                .onSuccess { location ->
                    _state.update { it.copy(phase = Phase.Loaded, location = location) }
                }
                .onFailure { error ->
                    _state.update { it.copy(phase = Phase.Error, errorMessage = error.message) }
                }
        }
    }
}
