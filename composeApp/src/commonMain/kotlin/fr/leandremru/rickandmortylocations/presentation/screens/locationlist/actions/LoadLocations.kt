package fr.leandremru.rickandmortylocations.presentation.screens.locationlist.actions

import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListAction
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListStore
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListUiState.Phase

/** Loads the locations from the repository and updates the UI state accordingly. */
data object LoadLocations : LocationListAction {
    override fun LocationListStore.reduce() {
        updateState { copy(phase = Phase.Loading, errorMessage = null) }
        fetchData(source = { repository.getLocations() }) {
            onSuccess { locations ->
                updateState { copy(phase = Phase.Loaded, locations = locations) }
            }
            onFailure { error ->
                updateState { copy(phase = Phase.Error, errorMessage = error.message) }
            }
        }
    }
}
