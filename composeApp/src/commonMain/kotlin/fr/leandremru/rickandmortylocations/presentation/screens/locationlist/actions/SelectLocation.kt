package fr.leandremru.rickandmortylocations.presentation.screens.locationlist.actions

import fr.leandremru.rickandmortylocations.domain.model.Location
import fr.leandremru.rickandmortylocations.presentation.navigation.AppNavigator
import fr.leandremru.rickandmortylocations.presentation.navigation.LocationDetailRoute
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListAction
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListStore

/** Navigates to the detail screen for the selected location. */
data class SelectLocation(val location: Location) : LocationListAction {
    override fun LocationListStore.reduce() {
        AppNavigator.navigate(LocationDetailRoute(location.id))
    }
}
