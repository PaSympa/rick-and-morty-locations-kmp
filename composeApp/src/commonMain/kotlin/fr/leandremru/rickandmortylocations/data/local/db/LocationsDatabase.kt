package fr.leandremru.rickandmortylocations.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import fr.leandremru.rickandmortylocations.data.local.dao.LocationDao
import kotlinx.coroutines.Dispatchers

/**
 * Room database holding the locally cached locations.
 *
 * Built from a platform-specific [RoomDatabase.Builder] provided via Koin
 * (see `databaseBuilderModule` in the DI layer). The KMP `expect`/`actual`
 * boundary lives in [LocationsDatabaseConstructor], whose `actual`
 * implementations are generated automatically by the Room compiler.
 */
@Database(
    entities = [LocationEntity::class],
    version = 1,
    exportSchema = false,
)
@ConstructedBy(LocationsDatabaseConstructor::class)
abstract class LocationsDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}

/** Room generates the `actual` for every target — no manual code required. */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LocationsDatabaseConstructor : RoomDatabaseConstructor<LocationsDatabase> {
    override fun initialize(): LocationsDatabase
}

/**
 * Finalises a [RoomDatabase.Builder] into a ready-to-use [LocationsDatabase].
 * Uses the bundled cross-platform SQLite driver so the same code works on
 * Android and Desktop without any platform branching here.
 */
fun getLocationsDatabase(builder: RoomDatabase.Builder<LocationsDatabase>): LocationsDatabase =
    builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
