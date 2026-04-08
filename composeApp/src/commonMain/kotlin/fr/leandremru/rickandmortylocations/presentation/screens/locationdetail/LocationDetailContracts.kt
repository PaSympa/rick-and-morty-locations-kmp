package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * Immutable UI state of the location detail screen.
 *
 * @property phase        Current rendering phase (loading / loaded / error).
 * @property location     Loaded location, only set when [phase] is `Loaded`.
 * @property errorMessage Last error message, only set when [phase] is `Error`.
 */
data class LocationDetailUiState(
    val phase: Phase = Phase.Loading,
    val location: Location? = null,
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

sealed interface LocationDetailAction {
    data object Retry : LocationDetailAction
}
