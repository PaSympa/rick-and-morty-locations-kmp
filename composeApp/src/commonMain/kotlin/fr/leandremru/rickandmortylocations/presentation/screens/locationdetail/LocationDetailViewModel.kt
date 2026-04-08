package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.core.presentation.StoreViewModel

/** ViewModel for the location detail screen. */
class LocationDetailViewModel(
    store: LocationDetailStore,
) : StoreViewModel<LocationDetailUiState, LocationDetailStore, LocationDetailAction>(store)
