package fr.leandremru.rickandmortylocations

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.leandremru.rickandmortylocations.core.audio.AudioManager
import fr.leandremru.rickandmortylocations.core.di.initKoin
import fr.leandremru.rickandmortylocations.presentation.screens.desktop.LocationsDesktopScreen
import fr.leandremru.rickandmortylocations.presentation.theme.RnMTheme

/** Point d'entrée Desktop : Koin + theme song, puis ouvre la fenêtre master-detail. */
fun main() {
    val koin = initKoin().koin
    koin.get<AudioManager>().playThemeSong()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Rick & Morty Locations",
        ) {
            RnMTheme {
                LocationsDesktopScreen()
            }
        }
    }
}
