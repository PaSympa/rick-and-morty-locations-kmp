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
 * before being returned. Errors propagate through standard exceptions — the
 * MVI store wraps them in a [Result] via its `fetchData` helper.
 */
class LocationRepositoryImpl(
    private val api: LocationApi,
    private val dao: LocationDao,
) : LocationRepository {

    override suspend fun getLocations(): List<Location> {
        val cached = dao.getAll()
        if (cached.isNotEmpty()) return cached.map { it.toDomain() }
        val fresh = api.fetchLocations().results
        val entities = fresh.map { it.toEntity() }
        dao.upsertAll(entities)
        return entities.map { it.toDomain() }
    }

    override suspend fun getLocationById(id: Int): Location {
        val cached = dao.getById(id)
        if (cached != null) return cached.toDomain()
        val freshDto = api.fetchLocation(id)
        val entity = freshDto.toEntity()
        dao.upsertAll(listOf(entity))
        return entity.toDomain()
    }
}
