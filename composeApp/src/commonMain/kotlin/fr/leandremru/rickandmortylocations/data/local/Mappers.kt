package fr.leandremru.rickandmortylocations.data.local

import fr.leandremru.rickandmortylocations.data.local.db.LocationEntity
import fr.leandremru.rickandmortylocations.data.remote.dto.LocationDto
import fr.leandremru.rickandmortylocations.domain.model.Location

private const val ID_SEPARATOR = ","

/** Converts a [LocationEntity] read from the local DB into the domain [Location]. */
fun LocationEntity.toDomain(): Location = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residentIds.parseIdList(),
    createdAt = createdAt,
)

/**
 * Converts a remote [LocationDto] into a persistable [LocationEntity].
 *
 * Resident URLs are reduced to their numeric IDs and joined as a CSV string.
 * That is enough for our use-case (no SQL queries on individual residents),
 * and avoids introducing a separate `resident` table just for a list lookup.
 */
fun LocationDto.toEntity(): LocationEntity = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residents
        .mapNotNull { it.substringAfterLast('/').toIntOrNull() }
        .joinToString(ID_SEPARATOR),
    createdAt = created,
)

private fun String.parseIdList(): List<Int> =
    if (isEmpty()) emptyList()
    else split(ID_SEPARATOR).mapNotNull { it.toIntOrNull() }
