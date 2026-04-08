package fr.leandremru.rickandmortylocations.domain.repository

import fr.leandremru.rickandmortylocations.domain.model.Location

/**
 * Domain contract for accessing locations.
 * Implementations live in the data layer and may combine remote + local sources.
 * Methods throw on failure; the presentation layer wraps them via the MVI store.
 */
interface LocationRepository {

    /** Returns all available locations. */
    suspend fun getLocations(): List<Location>

    /**
     * Returns the detail of a single location.
     *
     * @param id Identifier of the location to load.
     */
    suspend fun getLocationById(id: Int): Location
}
