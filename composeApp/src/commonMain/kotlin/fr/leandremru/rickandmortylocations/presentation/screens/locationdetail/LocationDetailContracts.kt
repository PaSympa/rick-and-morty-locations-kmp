package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * Immutable UI state of the location detail screen.
 *
 * `requestedId` is tracked here (not in the constructor) so the same VM
 * instance can serve consecutive selections on Desktop master-detail.
 */
data class LocationDetailUiState(
    val phase: Phase = Phase.Loading,
    val requestedId: Int? = null,
    val location: Location? = null,
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

sealed interface LocationDetailAction {
    data class Load(val id: Int) : LocationDetailAction
    data object Retry : LocationDetailAction
}
