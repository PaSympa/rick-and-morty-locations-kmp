package fr.leandremru.rickandmortylocations.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Root theme of the Rick & Morty Locations app.
 *
 * Wraps Material 3 with the brand color scheme and typography so every screen
 * automatically inherits the design system. Both [App] (mobile) and the desktop
 * `main` window mount this composable as their outermost UI layer — no screen
 * should ever wrap itself in a `MaterialTheme` directly.
 */
@Composable
fun RnMTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) RnMDarkColorScheme else RnMLightColorScheme,
        typography = RnMTypography,
        content = content,
    )
}
