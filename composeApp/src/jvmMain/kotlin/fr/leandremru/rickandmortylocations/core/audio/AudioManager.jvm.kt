package fr.leandremru.rickandmortylocations.core.audio

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/**
 * Desktop `actual` of [AudioManager] backed by `javax.sound.sampled.Clip`.
 *
 * Only WAV is supported (native to `javax.sound.sampled`); a missing or
 * malformed file leaves the corresponding clip `null` and turns the matching
 * `play*` call into a no-op.
 */
actual class AudioManager {

    private val portalClip: Clip? = loadClip("portal_click.wav")
    private val themeClip: Clip? = loadClip("theme_song.wav")
    private var themePlayed: Boolean = false

    actual fun playPortalClick() {
        portalClip?.let { clip ->
            clip.framePosition = 0
            clip.start()
        }
    }

    actual fun playThemeSong() {
        if (themePlayed) return
        themePlayed = true
        themeClip?.let { clip ->
            clip.framePosition = 0
            clip.start()
        }
    }

    private fun loadClip(resourceName: String): Clip? = runCatching {
        val resource = javaClass.classLoader?.getResourceAsStream(resourceName) ?: return null
        val audioStream = AudioSystem.getAudioInputStream(resource.buffered())
        AudioSystem.getClip().apply { open(audioStream) }
    }.getOrNull()
}
