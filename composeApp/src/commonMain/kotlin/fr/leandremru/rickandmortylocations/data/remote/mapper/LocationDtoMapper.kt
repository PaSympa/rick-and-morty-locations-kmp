package fr.leandremru.rickandmortylocations.data.remote.mapper

import fr.leandremru.rickandmortylocations.data.remote.dto.LocationDto
import fr.leandremru.rickandmortylocations.domain.model.Location

/** Converts a [LocationDto] into the domain [Location], extracting resident IDs from their URLs. */
fun LocationDto.toDomain(): Location = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residents.mapNotNull { url ->
        url.substringAfterLast('/').toIntOrNull()
    },
    createdAt = created,
)
