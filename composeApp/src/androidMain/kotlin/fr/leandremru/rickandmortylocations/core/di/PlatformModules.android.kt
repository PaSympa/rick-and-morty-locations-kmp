package fr.leandremru.rickandmortylocations.core.di

import androidx.room.Room
import androidx.room.RoomDatabase
import fr.leandremru.rickandmortylocations.data.local.db.LocationsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/** Android implementation: persistent SQLite file in the app's data directory. */
private val databaseBuilderModule: Module = module {
    single<RoomDatabase.Builder<LocationsDatabase>> {
        val context = androidContext()
        val dbFile = context.getDatabasePath("locations.db")
        Room.databaseBuilder<LocationsDatabase>(
            context = context,
            name = dbFile.absolutePath,
        )
    }
}

actual fun platformModules(): List<Module> = listOf(databaseBuilderModule)
