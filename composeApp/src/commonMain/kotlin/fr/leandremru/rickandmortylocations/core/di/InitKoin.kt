package fr.leandremru.rickandmortylocations.core.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Single entry point for Koin initialization, called once per platform
 * (Android `Application.onCreate`, Desktop `main` before the window opens).
 *
 * @param extraConfig optional platform-specific configuration block, used on
 *                    Android to pass `androidContext(this@RickAndMortyApp)`.
 */
fun initKoin(extraConfig: KoinAppDeclaration? = null) {
    startKoin {
        extraConfig?.invoke(this)
        modules(platformModules())
        modules(networkModule, databaseModule, repositoryModule)
    }
}
