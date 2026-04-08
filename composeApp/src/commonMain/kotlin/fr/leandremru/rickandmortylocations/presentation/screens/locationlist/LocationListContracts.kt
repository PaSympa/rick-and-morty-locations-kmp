package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import fr.leandremru.rickandmortylocations.core.presentation.StoreAction
import fr.leandremru.rickandmortylocations.domain.model.Location

/** Immutable UI state of the locations list screen. */
data class LocationListUiState(
    val phase: Phase = Phase.Loading,
    val locations: List<Location> = emptyList(),
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

/** Marker interface for every action handled by [LocationListStore]. */
interface LocationListAction : StoreAction<LocationListUiState, LocationListStore>
