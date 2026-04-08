package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.core.presentation.Store
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.actions.LoadLocationDetail

/** MVI store backing the location detail screen. */
class LocationDetailStore(
    val locationId: Int,
    val repository: LocationRepository,
) : Store<LocationDetailUiState>(LocationDetailUiState()) {

    init {
        LoadLocationDetail.execute(from = this)
    }
}
