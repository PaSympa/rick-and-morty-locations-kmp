package fr.leandremru.rickandmortylocations.core.audio

import android.content.Context

/**
 * Idiomatic factory for the Android [AudioManager] from an application [Context].
 * Wired in Koin as `single { androidContext().createAudioManager() }`.
 */
fun Context.createAudioManager(): AudioManager = AudioManager(this)
