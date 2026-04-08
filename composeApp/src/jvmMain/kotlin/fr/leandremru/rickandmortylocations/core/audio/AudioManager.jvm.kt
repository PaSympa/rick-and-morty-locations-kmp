package fr.leandremru.rickandmortylocations.core.audio

import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/**
 * Desktop `actual` of [AudioManager] backed by `javax.sound.sampled.Clip`.
 *
 * Audio files are looked up on the JVM classpath at construction time. If a
 * file is missing or malformed, the corresponding clip stays `null` and the
 * matching `play*` method becomes a silent no-op.
 *
 * Note: `javax.sound.sampled` only supports WAV / AIFF / AU natively — MP3
 * files would need an extra SPI provider, which is why we use WAV.
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
