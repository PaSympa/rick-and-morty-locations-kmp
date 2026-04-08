package fr.leandremru.rickandmortylocations

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import fr.leandremru.rickandmortylocations.core.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Rick & Morty Locations",
        ) {
            App()
        }
    }
}
