package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * Immutable UI state of the location detail screen.
 *
 * `requestedId` is tracked here (not in the constructor) so the same VM
 * instance can serve consecutive selections on Desktop master-detail.
 *
 * @property phase        Current rendering phase (loading / loaded / error).
 * @property requestedId  Id of the last location asked to load, or `null` before the first request.
 * @property location     Loaded location, only set when [phase] is `Loaded`.
 * @property errorMessage Last error message, only set when [phase] is `Error`.
 */
data class LocationDetailUiState(
    val phase: Phase = Phase.Loading,
    val requestedId: Int? = null,
    val location: Location? = null,
    val errorMessage: String? = null,
) {
    enum class Phase { Loading, Loaded, Error }
}

sealed interface LocationDetailAction {
    data class Load(val id: Int) : LocationDetailAction
    data object Retry : LocationDetailAction
}
