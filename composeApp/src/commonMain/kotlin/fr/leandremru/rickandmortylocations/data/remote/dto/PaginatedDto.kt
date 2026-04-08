package fr.leandremru.rickandmortylocations.data.remote.dto

import kotlinx.serialization.Serializable

/** Generic envelope for any paginated endpoint of the Rick and Morty API. */
@Serializable
data class PaginatedDto<T>(
    val info: InfoDto,
    val results: List<T>,
)
