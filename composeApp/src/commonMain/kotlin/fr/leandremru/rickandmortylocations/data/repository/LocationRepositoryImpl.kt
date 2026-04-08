package fr.leandremru.rickandmortylocations.data.repository

import fr.leandremru.rickandmortylocations.data.local.dao.LocationDao
import fr.leandremru.rickandmortylocations.data.local.mapper.toDomain
import fr.leandremru.rickandmortylocations.data.local.mapper.toEntity
import fr.leandremru.rickandmortylocations.data.remote.api.LocationApi
import fr.leandremru.rickandmortylocations.domain.model.Location
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository

/**
 * Cache-then-network implementation of [LocationRepository].
 *
 * Both methods read from the local DAO first and only hit the network when the
 * requested data is missing from the cache; freshly fetched DTOs are persisted
 * before being returned. Failures are surfaced via [Result] so the presentation
 * layer can handle them as plain state transitions.
 */
class LocationRepositoryImpl(
    private val api: LocationApi,
    private val dao: LocationDao,
) : LocationRepository {

    override suspend fun getLocations(): Result<List<Location>> = runCatching {
        val cached = dao.getAll()
        if (cached.isNotEmpty()) {
            cached.map { it.toDomain() }
        } else {
            val fresh = api.fetchLocations().results
            val entities = fresh.map { it.toEntity() }
            dao.upsertAll(entities)
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLocationById(id: Int): Result<Location> = runCatching {
        val cached = dao.getById(id)
        if (cached != null) {
            cached.toDomain()
        } else {
            val freshDto = api.fetchLocation(id)
            val entity = freshDto.toEntity()
            dao.upsertAll(listOf(entity))
            entity.toDomain()
        }
    }
}
