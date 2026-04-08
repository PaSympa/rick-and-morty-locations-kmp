package fr.leandremru.rickandmortylocations

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import fr.leandremru.rickandmortylocations.presentation.navigation.AppNavHost

/**
 * Application root composable. Wraps the navigation host in the Material theme.
 * The dedicated brand theme will replace [MaterialTheme] in a later phase.
 */
@Composable
fun App() {
    MaterialTheme {
        AppNavHost()
    }
}
