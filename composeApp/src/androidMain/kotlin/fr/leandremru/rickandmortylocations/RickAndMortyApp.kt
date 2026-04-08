package fr.leandremru.rickandmortylocations

import android.app.Application
import fr.leandremru.rickandmortylocations.core.di.initKoin
import org.koin.android.ext.koin.androidContext

/**
 * Android Application class — initializes Koin once for the whole process.
 * Referenced in `AndroidManifest.xml` via `android:name=".RickAndMortyApp"`.
 */
class RickAndMortyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@RickAndMortyApp)
        }
    }
}
