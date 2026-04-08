package fr.leandremru.rickandmortylocations.core.audio

import android.content.Context
import android.media.MediaPlayer

/**
 * Android `actual` of [AudioManager], backed by [MediaPlayer].
 *
 * Resource lookup is done at runtime via `Resources.getIdentifier(...)` so the
 * build does not fail if the audio files are not yet present in `res/raw/`.
 * Missing files become silent no-ops.
 *
 * The constructor takes a [Context] because [MediaPlayer.create] needs one.
 * It is wired through the dedicated `Context.createAudioManager()` extension
 * function (see `ContextExtensions.android.kt`) so the Koin module reads as
 * a single, idiomatic line.
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
