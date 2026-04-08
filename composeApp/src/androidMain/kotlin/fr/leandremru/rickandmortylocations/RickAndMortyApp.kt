package fr.leandremru.rickandmortylocations

import android.app.Application
import fr.leandremru.rickandmortylocations.core.audio.AudioManager
import fr.leandremru.rickandmortylocations.core.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Android Application class — initializes Koin once for the whole process,
 * then plays the launch theme song through the cross-native [AudioManager].
 * Referenced in `AndroidManifest.xml` via `android:name=".RickAndMortyApp"`.
 */
class RickAndMortyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val koin = initKoin {
            androidContext(this@RickAndMortyApp)
        }.koin
        koin.get<AudioManager>().playThemeSong()
    }
}
