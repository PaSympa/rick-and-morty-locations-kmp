package fr.leandremru.rickandmortylocations

import androidx.compose.runtime.Composable
import fr.leandremru.rickandmortylocations.presentation.navigation.AppNavHost
import fr.leandremru.rickandmortylocations.presentation.theme.RnMTheme

/** Application root composable. Wraps the navigation host in the brand theme. */
@Composable
fun App() {
    RnMTheme {
        AppNavHost()
    }
}
