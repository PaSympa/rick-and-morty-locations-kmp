package fr.leandremru.rickandmortylocations.presentation.screens.locationdetail

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * UDF contract of the location detail screen.
 *
 * The state stays stateless about which id was requested last (`requestedId`)
 * so the same ViewModel can be reused on Desktop, where the user keeps switching
 * locations from the list pane on a single screen instance.
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
    /** Load the location with the given id, replacing whatever the screen was showing. */
    data class Load(val id: Int) : LocationDetailAction

    /** Reload after a previous failure. Uses the last requested id. */
    data object Retry : LocationDetailAction
}
