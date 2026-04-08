package fr.leandremru.rickandmortylocations.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Wire-level representation of a location as returned by the Rick and Morty API.
 *
 * @property id         Unique identifier.
 * @property name       Display name.
 * @property type       Free-form type label.
 * @property dimension  Free-form dimension label.
 * @property residents  Character URLs (e.g. ".../api/character/42"), mapped to IDs in the data layer.
 * @property url        Canonical URL of this location on the API.
 * @property created    Creation timestamp returned by the API.
 */
@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String?,
)
