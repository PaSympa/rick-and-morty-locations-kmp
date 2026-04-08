package fr.leandremru.rickandmortylocations.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Root theme — wraps Material 3 with the brand color scheme and typography. */
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
