package fr.leandremru.rickandmortylocations.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Brand palette inspired by the Rick & Morty show — acid portal green,
 * schwifty pink, Morty yellow, Rick lab cyan, toxic purple.
 */
internal object RnMColors {
    val PortalGreen = Color(0xFF97CE4C)
    val PickleRick = Color(0xFF6FA84B)
    val SchwiftyPink = Color(0xFFFF6BD6)
    val MortyYellow = Color(0xFFFCDB1E)
    val RickCyan = Color(0xFF00B5C5)
    val ToxicPurple = Color(0xFF8F3FB2)
    val SpaceBlack = Color(0xFF0A0E14)
    val DimensionBlue = Color(0xFF1A2A3A)
    val LabCoat = Color(0xFFF5F7F0)
}

internal val RnMLightColorScheme = lightColorScheme(
    primary = RnMColors.PortalGreen,
    onPrimary = RnMColors.SpaceBlack,
    primaryContainer = RnMColors.PortalGreen.copy(alpha = 0.18f),
    onPrimaryContainer = RnMColors.PickleRick,
    secondary = RnMColors.SchwiftyPink,
    onSecondary = RnMColors.SpaceBlack,
    tertiary = RnMColors.RickCyan,
    onTertiary = RnMColors.SpaceBlack,
    background = RnMColors.LabCoat,
    onBackground = RnMColors.SpaceBlack,
    surface = Color.White,
    onSurface = RnMColors.SpaceBlack,
    surfaceVariant = RnMColors.LabCoat,
    onSurfaceVariant = RnMColors.SpaceBlack.copy(alpha = 0.7f),
)

internal val RnMDarkColorScheme = darkColorScheme(
    primary = RnMColors.PortalGreen,
    onPrimary = RnMColors.SpaceBlack,
    primaryContainer = RnMColors.PickleRick.copy(alpha = 0.30f),
    onPrimaryContainer = RnMColors.PortalGreen,
    secondary = RnMColors.SchwiftyPink,
    onSecondary = RnMColors.SpaceBlack,
    tertiary = RnMColors.RickCyan,
    onTertiary = RnMColors.SpaceBlack,
    background = RnMColors.SpaceBlack,
    onBackground = RnMColors.LabCoat,
    surface = RnMColors.DimensionBlue,
    onSurface = RnMColors.LabCoat,
    surfaceVariant = RnMColors.DimensionBlue,
    onSurfaceVariant = RnMColors.LabCoat.copy(alpha = 0.7f),
)
