package fr.leandremru.rickandmortylocations.core.di

import androidx.room.RoomDatabase
import fr.leandremru.rickandmortylocations.data.local.dao.LocationDao
import fr.leandremru.rickandmortylocations.data.local.db.LocationsDatabase
import fr.leandremru.rickandmortylocations.data.local.db.getLocationsDatabase
import fr.leandremru.rickandmortylocations.data.remote.api.LocationApi
import fr.leandremru.rickandmortylocations.data.remote.createHttpClient
import fr.leandremru.rickandmortylocations.data.repository.LocationRepositoryImpl
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailStore
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListStore
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import org.koin.core.module.dsl.viewModel
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

/** Per-screen MVI stores and ViewModels. */
val viewModelModule = module {
    // Locations list
    factory { LocationListStore(get()) }
    viewModel { LocationListViewModel(get()) }

    // Location detail (parameterized by locationId from the navigation route)
    viewModel { (locationId: Int) ->
        LocationDetailViewModel(
            store = LocationDetailStore(locationId = locationId, repository = get()),
        )
    }
}
