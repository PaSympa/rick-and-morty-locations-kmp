package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.actions

import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailAction
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailStore
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailUiState.Phase

/** Loads the location detail for the store's [LocationDetailStore.locationId]. */
data object LoadLocationDetail : LocationDetailAction {
    override fun LocationDetailStore.reduce() {
        updateState { copy(phase = Phase.Loading, errorMessage = null) }
        fetchData(source = { repository.getLocationById(locationId) }) {
            onSuccess { location ->
                updateState { copy(phase = Phase.Loaded, location = location) }
            }
            onFailure { error ->
                updateState { copy(phase = Phase.Error, errorMessage = error.message) }
            }
        }
    }
}
