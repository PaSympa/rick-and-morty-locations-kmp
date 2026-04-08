package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import fr.leandremru.rickandmortylocations.core.presentation.Store
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.actions.LoadLocations

/** MVI store backing the locations list screen. */
class LocationListStore(
    val repository: LocationRepository,
) : Store<LocationListUiState>(LocationListUiState()) {

    init {
        LoadLocations.execute(from = this)
    }
}
