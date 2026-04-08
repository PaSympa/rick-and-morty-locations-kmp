package fr.leandremru.rickandmortylocations.domain.repository

import fr.leandremru.rickandmortylocations.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Domain contract for accessing locations.
 *
 * The list endpoint returns a [Flow] so the presentation layer can react to
 * cache updates without manually polling: the data layer is free to refresh
 * the local source from the network at any time, the UI just observes the
 * stream. The single-detail endpoint is `suspend` because there is nothing to
 * observe — a detail is loaded once on user request.
 */
interface LocationRepository {

    /** Stream of locations, backed by the local cache and refreshed from the network on miss. */
    fun getLocations(): Flow<List<Location>>

    /**
     * Returns the detail of a single location.
     *
     * @param id Identifier of the location to load.
     */
    suspend fun getLocationById(id: Int): Location
}
