package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * UDF contract of the locations list screen.
 *
 * - [LocationListUiState] is the immutable state observed by the composable.
 * - [LocationListAction] enumerates every user-driven event the ViewModel can react to.
 *
 * Selection is intentionally NOT modeled as an action here: navigating to the detail
 * is a UI concern, handled by the screen via a callback to the NavHost. The ViewModel
 * stays focused on data loading.
 */
data class LocationListUiState(
    val phase: Phase = Phase.Loading,
    val locations: List<Location> = emptyList(),
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

sealed interface LocationListAction {
    /** Initial load triggered by the screen. */
    data object Load : LocationListAction

    /** Reload after a previous failure. Same effect as [Load], but explicit for readability. */
    data object Retry : LocationListAction
}
