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
 * The list endpoint exposes a reactive [Flow] backed by Room's `observeAll()`:
 * the UI subscribes once and reacts to every cache update for free. The fetch
 * strategy lives in [refreshIfEmpty], called from `onStart`:
 *
 *  1. The Flow starts emitting whatever is currently in Room (possibly empty).
 *  2. In parallel, `onStart` checks if the cache is empty; if it is, the API
 *     is called and the result is upserted into Room — which immediately
 *     triggers a fresh emission downstream because the Flow observes the
 *     same table.
 *
 * The single-detail path stays `suspend` since there is nothing to observe:
 * we serve from cache when available, otherwise hit the API and persist the
 * result for later subscribers of [getLocations].
 */
class LocationRepositoryImpl(
    private val api: LocationApi,
    private val dao: LocationDao,
) : LocationRepository {

    override fun getLocations(): Flow<List<Location>> =
        dao.observeAll()
            .onStart { refreshIfEmpty() }
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getLocationById(id: Int): Location {
        dao.getById(id)?.let { return it.toDomain() }
        val entity = api.fetchLocation(id).toEntity()
        dao.upsertAll(listOf(entity))
        return entity.toDomain()
    }

    /** Pulls the first page of locations from the API only if the local cache is empty. */
    private suspend fun refreshIfEmpty() {
        if (dao.getAll().isNotEmpty()) return
        val fresh = api.fetchLocations().results.map { it.toEntity() }
        dao.upsertAll(fresh)
    }
}
