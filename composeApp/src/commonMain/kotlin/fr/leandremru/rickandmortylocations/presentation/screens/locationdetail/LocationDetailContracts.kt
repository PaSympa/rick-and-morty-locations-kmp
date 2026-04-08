package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.core.presentation.StoreAction
import fr.leandremru.rickandmortylocations.domain.model.Location

/** Immutable UI state of the location detail screen. */
data class LocationDetailUiState(
    val phase: Phase = Phase.Loading,
    val location: Location? = null,
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

/** Marker interface for every action handled by [LocationDetailStore]. */
interface LocationDetailAction : StoreAction<LocationDetailUiState, LocationDetailStore>
