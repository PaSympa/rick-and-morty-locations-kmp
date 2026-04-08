package fr.leandremru.rickandmortylocations.core.di

import androidx.room.RoomDatabase
import fr.leandremru.rickandmortylocations.data.local.dao.LocationDao
import fr.leandremru.rickandmortylocations.data.local.db.LocationsDatabase
import fr.leandremru.rickandmortylocations.data.local.db.getLocationsDatabase
import fr.leandremru.rickandmortylocations.data.remote.api.LocationApi
import fr.leandremru.rickandmortylocations.data.remote.createHttpClient
import fr.leandremru.rickandmortylocations.data.repository.LocationRepositoryImpl
import fr.leandremru.rickandmortylocations.domain.repository.LocationRepository
import fr.leandremru.rickandmortylocations.presentation.navigation.Destination
import fr.leandremru.rickandmortylocations.presentation.screens.locationdetail.LocationDetailViewModel
import fr.leandremru.rickandmortylocations.presentation.screens.locationlist.LocationListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Ktor `HttpClient` and remote services. */
val remoteModule: Module = module {
    single { createHttpClient() }
    single { LocationApi(get()) }
}

/** Room database + DAO. The builder itself is provided by [platformModules]. */
val databaseModule: Module = module {
    single<LocationsDatabase> { getLocationsDatabase(get<RoomDatabase.Builder<LocationsDatabase>>()) }
    single<LocationDao> { get<LocationsDatabase>().locationDao() }
}

/** Domain contracts implemented by the data layer. */
val repositoryModule: Module = module {
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
}

/** Per-screen ViewModels. */
val viewModelModule: Module = module {
    viewModel { LocationListViewModel(get()) }
    viewModel { (navKey: Destination.LocationDetail) ->
        LocationDetailViewModel(navKey = navKey, repository = get(), audioManager = get())
    }
}

/** Aggregates every cross-platform Koin module so [initKoin] only has to call one function. */
fun sharedModules(): List<Module> = listOf(
    remoteModule,
    databaseModule,
    repositoryModule,
    viewModelModule,
)
