package fr.leandremru.rickandmortylocations.domain.repository

import fr.leandremru.rickandmortylocations.domain.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Domain contract for accessing locations.
 *
 * The list endpoint returns a [Flow] so the UI can react to cache updates
 * without polling. The single-detail endpoint stays `suspend` because there
 * is nothing to observe.
 */
interface LocationRepository {

    fun getLocations(): Flow<List<Location>>

    suspend fun getLocationById(id: Int): Location
}
