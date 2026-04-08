package fr.leandremru.rickandmortylocations.data.repository

import fr.leandremru.rickandmortylocations.data.local.dao.LocationDao
import fr.leandremru.rickandmortylocations.data.local.toDomain
import fr.leandremru.rickandmortylocations.data.local.toEntity
import fr.leandremru.rickandmortylocations.data.remote.api.LocationApi
import fr.leandremru.rickandmortylocations.domain.model.Location
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Cache-then-network implementation of [LocationRepository].
 *
 * Room is the source of truth: the list endpoint exposes a reactive [Flow]
 * backed by `dao.observeAll()` and the network is only hit when the local cache
 * is empty (see [refreshIfEmpty]). This makes Room and Ktor act as two real
 * sources rather than the API alimenting a passive cache.
 */
class LocationRepositoryImpl(
    private val api: LocationApi,
    private val dao: LocationDao,
) : LocationRepository {

    /**
     * Stream of locations.
     *
     * Subscribers receive whatever sits in Room first, then any later upsert
     * (including the one triggered by [refreshIfEmpty] on a cold start) emits
     * downstream automatically.
     *
     * @return a hot stream observing the local cache.
     */
    override fun getLocations(): Flow<List<Location>> =
        dao.observeAll()
            .onStart { refreshIfEmpty() }
            .map { entities -> entities.map { it.toDomain() } }

    /**
     * @param id identifier of the location to load.
     * @return the cached location if present, otherwise the freshly fetched and
     *         persisted one — propagated to future [getLocations] subscribers.
     */
    override suspend fun getLocationById(id: Int): Location {
        dao.getById(id)?.let { return it.toDomain() }
        val entity = api.fetchLocation(id).toEntity()
        dao.upsertAll(listOf(entity))
        return entity.toDomain()
    }

    /** Pulls the first page from the API only if the local cache is empty. */
    private suspend fun refreshIfEmpty() {
        if (dao.getAll().isNotEmpty()) return
        val fresh = api.fetchLocations().results.map { it.toEntity() }
        dao.upsertAll(fresh)
    }
}
