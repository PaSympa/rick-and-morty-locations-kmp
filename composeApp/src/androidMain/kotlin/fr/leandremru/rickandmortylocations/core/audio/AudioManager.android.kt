package fr.leandremru.rickandmortylocations.core.audio

import android.content.Context
import android.media.MediaPlayer

/**
 * Android `actual` of [AudioManager], backed by [MediaPlayer].
 *
 * Resources are looked up at runtime via `getIdentifier(...)` so a missing audio
 * file becomes a silent no-op rather than a crash.
 */
actual class AudioManager(private val context: Context) {

    private var portalPlayer: MediaPlayer? = null
    private var themePlayer: MediaPlayer? = null

    actual fun playPortalClick() {
        runCatching {
            val resId = context.resources.getIdentifier("portal_click", "raw", context.packageName)
            if (resId == 0) return
            portalPlayer?.release()
            portalPlayer = MediaPlayer.create(context, resId)?.apply { start() }
        }
    }

    actual fun playThemeSong() {
        runCatching {
            if (themePlayer != null) return
            val resId = context.resources.getIdentifier("theme_song", "raw", context.packageName)
            if (resId == 0) return
            themePlayer = MediaPlayer.create(context, resId)?.apply { start() }
        }
    }
}
