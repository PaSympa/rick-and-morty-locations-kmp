package fr.leandremru.rickandmortylocations.core.di

import androidx.room.Room
import androidx.room.RoomDatabase
import fr.leandremru.rickandmortylocations.core.audio.AudioManager
import fr.leandremru.rickandmortylocations.data.local.db.LocationsDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

/** Persistent SQLite file in `~/.rick-and-morty-locations/locations.db`. */
private val databaseBuilderModule: Module = module {
    single<RoomDatabase.Builder<LocationsDatabase>> {
        val dbDir = File(System.getProperty("user.home"), ".rick-and-morty-locations")
        if (!dbDir.exists()) dbDir.mkdirs()
        val dbFile = File(dbDir, "locations.db")
        Room.databaseBuilder<LocationsDatabase>(name = dbFile.absolutePath)
    }
}

/** [AudioManager] no-arg actual (javax.sound.sampled). */
private val audioModule: Module = module {
    single { AudioManager() }
}

actual fun platformModules(): List<Module> = listOf(databaseBuilderModule, audioModule)
