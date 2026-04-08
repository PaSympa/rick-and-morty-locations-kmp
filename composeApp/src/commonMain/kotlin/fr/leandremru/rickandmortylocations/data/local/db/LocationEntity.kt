package fr.leandremru.rickandmortylocations.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the local locations cache.
 *
 * @property id           Unique identifier of the location.
 * @property name         Display name.
 * @property type         Free-form type label.
 * @property dimension    Free-form dimension label.
 * @property residentIds  Comma-separated list of resident character IDs.
 * @property createdAt    Creation timestamp returned by the API.
 */
@Entity(tableName = LOCATION_TABLE)
data class LocationEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentIds: String,
    val createdAt: String?,
)

const val LOCATION_TABLE = "location_table"
