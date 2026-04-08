package fr.leandremru.rickandmortylocations.data.local

import fr.leandremru.rickandmortylocations.data.local.db.LocationEntity
import fr.leandremru.rickandmortylocations.data.remote.dto.LocationDto
import fr.leandremru.rickandmortylocations.domain.model.Location

private const val ID_SEPARATOR = ","

/** [LocationEntity] → domain [Location]. */
fun LocationEntity.toDomain(): Location = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residentIds.parseIdList(),
    createdAt = createdAt,
)

/**
 * [LocationDto] → [LocationEntity].
 *
 * Resident URLs are reduced to numeric ids and stored as a CSV string —
 * we never query individual residents, so a separate `resident` table would
 * be overkill.
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
