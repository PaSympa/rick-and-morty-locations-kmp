package fr.leandremru.rickandmortylocations

import android.app.Application
import fr.leandremru.rickandmortylocations.core.audio.AudioManager
import fr.leandremru.rickandmortylocations.core.di.initKoin
import org.koin.android.ext.koin.androidContext

/** Application entry: starts Koin, then fires the launch theme song. */
class RickAndMortyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val koin = initKoin {
            androidContext(this@RickAndMortyApp)
        }.koin
        koin.get<AudioManager>().playThemeSong()
    }
}
