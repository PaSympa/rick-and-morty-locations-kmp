package fr.leandremru.rickandmortylocations.core.audio

/**
 * Cross-platform audio manager.
 *
 * Declared as `expect class` to demonstrate the canonical Kotlin Multiplatform
 * `expect / actual` mechanism: callers in `commonMain` only see this contract,
 * while each target ships its own native implementation:
 *
 *  - **Android** uses `MediaPlayer` and is built from a `Context` extension
 *    (`Context.createAudioManager()` in `androidMain`).
 *  - **Desktop (JVM)** uses `javax.sound.sampled.Clip` and is instantiated
 *    via the no-arg constructor.
 *
 * Both implementations are silent no-ops if the underlying audio resources are
 * missing, so the application never crashes because of audio.
 */
expect class AudioManager {

    /** Plays the short "portal" sound effect. Triggered when a location detail opens. */
    fun playPortalClick()

    /** Plays the application theme song once at launch. */
    fun playThemeSong()
}
