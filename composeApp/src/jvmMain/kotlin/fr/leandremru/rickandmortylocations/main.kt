package fr.leandremru.rickandmortylocations

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.leandremru.rickandmortylocations.core.di.initKoin
import fr.leandremru.rickandmortylocations.presentation.screens.desktop.LocationsDesktopScreen

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Rick & Morty Locations",
        ) {
            MaterialTheme {
                LocationsDesktopScreen()
            }
        }
    }
}
