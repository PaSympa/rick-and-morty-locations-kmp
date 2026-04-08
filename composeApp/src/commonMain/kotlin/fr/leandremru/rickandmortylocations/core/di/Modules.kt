package fr.leandremru.rickandmortylocations.core.di

import androidx.room.RoomDatabase
import fr.leandremru.rickandmortylocations.data.local.dao.LocationDao
import fr.leandremru.rickandmortylocations.data.local.db.LocationsDatabase
import fr.leandremru.rickandmortylocations.data.local.db.getLocationsDatabase
import fr.leandremru.rickandmortylocations.data.remote.api.LocationApi
import fr.leandremru.rickandmortylocations.data.remote.createHttpClient
import fr.leandremru.rickandmortylocations.data.repository.LocationRepositoryImpl
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import org.koin.dsl.module

/** Ktor `HttpClient` and remote services. */
val networkModule = module {
    single { createHttpClient() }
    single { LocationApi(get()) }
}

/**
 * Room database, DAOs and the [RoomDatabase.Builder] consumed here.
 * The builder itself comes from [platformModule] because each platform
 * builds it differently.
 */
val databaseModule = module {
    single<LocationsDatabase> { getLocationsDatabase(get<RoomDatabase.Builder<LocationsDatabase>>()) }
    single<LocationDao> { get<LocationsDatabase>().locationDao() }
}

/** Domain contracts implemented by the data layer. */
val repositoryModule = module {
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
}
