package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.actions

import fr.leandremru.rickandmortylocations.presentation.navigation.AppNavigator
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailAction
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailStore

/** Pops the current entry from the back stack. */
data object NavigateBack : LocationDetailAction {
    override fun LocationDetailStore.reduce() {
        AppNavigator.navigateBack()
    }
}
