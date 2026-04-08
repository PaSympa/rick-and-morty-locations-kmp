package fr.leandremru.rickandmortylocations.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import fr.leandremru.rickandmortylocations.data.local.db.LOCATION_TABLE
import fr.leandremru.rickandmortylocations.data.local.db.LocationEntity
import kotlinx.coroutines.flow.Flow

/** Room DAO for the local locations cache. */
@Dao
interface LocationDao {

    @Query("SELECT * FROM $LOCATION_TABLE")
    fun observeAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM $LOCATION_TABLE")
    suspend fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM $LOCATION_TABLE WHERE id = :id")
    suspend fun getById(id: Int): LocationEntity?

    @Upsert
    suspend fun upsertAll(items: List<LocationEntity>)

    @Query("DELETE FROM $LOCATION_TABLE")
    suspend fun clear()
}
