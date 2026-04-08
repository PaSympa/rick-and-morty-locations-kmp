package fr.leandremru.rickandmortylocations.core.di

import org.koin.core.module.Module

/** Per-platform Koin modules: Room builder + native [AudioManager]. */
expect fun platformModules(): List<Module>
