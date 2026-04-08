package fr.leandremru.rickandmortylocations.core.audio

/**
 * Cross-platform audio contract.
 *
 * Declared as `expect class` so each target ships its own backend
 * (`MediaPlayer` on Android, `javax.sound.sampled` on Desktop) without
 * leaking platform types to `commonMain`.
 */
expect class AudioManager {
    fun playPortalClick()
    fun playThemeSong()
}
