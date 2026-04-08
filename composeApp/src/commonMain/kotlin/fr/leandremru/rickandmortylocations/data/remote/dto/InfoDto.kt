package fr.leandremru.rickandmortylocations.data.remote.dto

import kotlinx.serialization.Serializable

/** Pagination metadata returned by the Rick and Morty API alongside any paginated list. */
@Serializable
data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?,
)
