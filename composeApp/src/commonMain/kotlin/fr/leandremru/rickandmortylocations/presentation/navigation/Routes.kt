package fr.leandremru.rickandmortylocations.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Type-safe navigation routes for the Rick & Morty Locations app. */
sealed interface AppRoute : NavKey

/** Locations list — start destination on mobile, also used by the Desktop master pane. */
@Serializable
data object LocationListRoute : AppRoute

/**
 * Detail of a single location.
 *
 * @property locationId Identifier of the location to display.
 */
@Serializable
data class LocationDetailRoute(val locationId: Int) : AppRoute
