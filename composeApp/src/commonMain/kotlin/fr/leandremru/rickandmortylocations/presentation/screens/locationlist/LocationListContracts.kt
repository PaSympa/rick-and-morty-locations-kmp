package fr.leandremru.rickandmortylocations.presentation.screens.locationlist

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * Immutable UI state of the locations list screen.
 *
 * @property phase        Current rendering phase (loading / loaded / error).
 * @property locations    Locations to display once [phase] is `Loaded`.
 * @property errorMessage Last error message, only set when [phase] is `Error`.
 */
data class LocationListUiState(
    val phase: Phase = Phase.Loading,
    val locations: List<Location> = emptyList(),
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

/**
 * User-driven events handled by [LocationListViewModel].
 *
 * Selecting a location is intentionally NOT modeled here: it is a UI concern
 * (navigation) handled by a screen-level callback so the same screen can be
 * reused on Desktop master-detail without going through navigation.
 */
sealed interface LocationListAction {
    data object Load : LocationListAction
    data object Retry : LocationListAction
}
