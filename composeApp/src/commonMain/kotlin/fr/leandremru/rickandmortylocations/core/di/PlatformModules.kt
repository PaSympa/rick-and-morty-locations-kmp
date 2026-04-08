package fr.leandremru.rickandmortylocations.core.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin modules.
 *
 * Each platform returns the list of modules that depend on native APIs:
 * the Room database builder, and (later) the audio manager. Returning a
 * `List<Module>` makes it trivial to add more platform-specific bindings
 * without changing the contract.
 */
expect fun platformModules(): List<Module>
