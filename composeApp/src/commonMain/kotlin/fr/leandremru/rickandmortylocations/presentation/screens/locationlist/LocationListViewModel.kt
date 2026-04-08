package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import fr.leandremru.rickandmortylocations.core.presentation.StoreViewModel

/** ViewModel for the locations list screen. */
class LocationListViewModel(
    store: LocationListStore,
) : StoreViewModel<LocationListUiState, LocationListStore, LocationListAction>(store)
