package fr.leandremru.rickandmortylocations.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Single entry point for Koin initialization, called once per platform
 * (Android `Application.onCreate`, Desktop `main` before the window opens).
 *
 * @param extraConfig optional platform-specific configuration block, used on
 *                    Android to pass `androidContext(this@RickAndMortyApp)`.
 * @return the started [KoinApplication] so callers can resolve dependencies
 *         right after init (e.g. to trigger the launch theme song).
 */
fun initKoin(extraConfig: KoinAppDeclaration? = null): KoinApplication =
    startKoin {
        extraConfig?.invoke(this)
        modules(platformModules() + sharedModules())
    }
