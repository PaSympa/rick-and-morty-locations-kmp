package fr.leandremru.rickandmortylocations.core.di

import androidx.room.Room
import androidx.room.RoomDatabase
import fr.leandremru.rickandmortylocations.data.local.db.LocationsDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

/**
 * Desktop implementation: persistent SQLite file in
 * `~/.rick-and-morty-locations/locations.db`. The directory is created on
 * first run if needed.
 */
private val databaseBuilderModule: Module = module {
    single<RoomDatabase.Builder<LocationsDatabase>> {
        val dbDir = File(System.getProperty("user.home"), ".rick-and-morty-locations")
        if (!dbDir.exists()) dbDir.mkdirs()
        val dbFile = File(dbDir, "locations.db")
        Room.databaseBuilder<LocationsDatabase>(name = dbFile.absolutePath)
    }
}

actual fun platformModules(): List<Module> = listOf(databaseBuilderModule)
