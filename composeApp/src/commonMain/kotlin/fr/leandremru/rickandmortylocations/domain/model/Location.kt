package fr.leandremru.rickandmortylocations.domain.model

/**
 * Pure domain model for a Rick and Morty location.
 *
 * @property id          Unique identifier.
 * @property name        Display name (e.g. "Earth (C-137)").
 * @property type        Free-form type (e.g. "Planet").
 * @property dimension   Free-form dimension (e.g. "Dimension C-137").
 * @property residentIds Ids of the characters living here, parsed from the API URLs.
 * @property createdAt   Creation timestamp returned by the API.
 */
data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentIds: List<Int>,
    val createdAt: String?,
) {
    val residentCount: Int get() = residentIds.size
}
