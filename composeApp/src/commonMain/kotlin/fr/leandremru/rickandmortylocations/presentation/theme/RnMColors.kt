package fr.leandremru.rickandmortylocations.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Brand palette for the Rick & Morty Locations app.
 *
 * Colors are inspired by the show's "portal green" and "interdimensional purple"
 * to give the design system a recognizable identity instead of leaving the UI on
 * Material's default purple/pink defaults. They are exposed both as raw [Color]s
 * (for one-off usage in components) and through Material 3 color schemes (for the
 * theme propagation).
 */
internal object RnMColors {

    val PortalGreen = Color(0xFF45B07C)
    val PortalGreenDark = Color(0xFF1F7A4F)
    val InterdimensionalPurple = Color(0xFF6E4AA8)
    val SchwiftyTeal = Color(0xFF2BB3C0)
    val SpaceBlack = Color(0xFF0F1419)
    val MoonGrey = Color(0xFFE8ECEF)
}

internal val RnMLightColorScheme = lightColorScheme(
    primary = RnMColors.PortalGreen,
    onPrimary = Color.White,
    secondary = RnMColors.InterdimensionalPurple,
    onSecondary = Color.White,
    tertiary = RnMColors.SchwiftyTeal,
    background = RnMColors.MoonGrey,
    surface = Color.White,
    onSurface = RnMColors.SpaceBlack,
)

internal val RnMDarkColorScheme = darkColorScheme(
    primary = RnMColors.PortalGreen,
    onPrimary = RnMColors.SpaceBlack,
    secondary = RnMColors.InterdimensionalPurple,
    onSecondary = Color.White,
    tertiary = RnMColors.SchwiftyTeal,
    background = RnMColors.SpaceBlack,
    surface = Color(0xFF1A2128),
    onSurface = RnMColors.MoonGrey,
)
