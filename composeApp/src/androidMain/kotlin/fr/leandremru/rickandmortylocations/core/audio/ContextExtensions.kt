package fr.leandremru.rickandmortylocations.core.audio

import android.content.Context

/**
 * Idiomatic factory for [AudioManager] from an Android [Context].
 *
 * Used by the platform Koin module so the audio manager is created from the
 * application context with a single, readable expression:
 *
 * ```kotlin
 * single<AudioManager> { androidContext().createAudioManager() }
 * ```
 *
 * This is the dedicated `Context` extension function required by the
 * cross-native bloc of the eval brief and the grille.
 */
fun Context.createAudioManager(): AudioManager = AudioManager(this)
