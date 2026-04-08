package fr.leandremru.rickandmortylocations.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.presentation.theme.RnMColors

/** Pill-shaped chip displaying a location type with a themed emoji and color. */
@Composable
fun RnMTypeChip(type: String, modifier: Modifier = Modifier) {
    val (emoji, color) = typeMeta(type)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = color.copy(alpha = 0.18f),
        contentColor = color,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = emoji, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = type.ifEmpty { "Unknown" }.uppercase(),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

/** Maps a location type to a representative emoji and brand color. */
private fun typeMeta(type: String): Pair<String, Color> = when (type.trim().lowercase()) {
    "planet" -> "🪐" to RnMColors.PortalGreen
    "cluster" -> "✨" to RnMColors.SchwiftyPink
    "space station" -> "🛸" to RnMColors.RickCyan
    "microverse" -> "⚛\uFE0F" to RnMColors.MortyYellow
    "tv" -> "📺" to RnMColors.ToxicPurple
    "resort" -> "🏖\uFE0F" to RnMColors.SchwiftyPink
    "fantasy town" -> "🏰" to RnMColors.MortyYellow
    "dream" -> "💭" to RnMColors.RickCyan
    "dimension" -> "🌀" to RnMColors.PortalGreen
    "miniverse" -> "🔬" to RnMColors.MortyYellow
    "menagerie" -> "🦄" to RnMColors.SchwiftyPink
    "game" -> "🎮" to RnMColors.ToxicPurple
    "customs" -> "🛂" to RnMColors.RickCyan
    "daycare" -> "🧸" to RnMColors.SchwiftyPink
    "elemental rings" -> "💍" to RnMColors.MortyYellow
    "spa" -> "💆" to RnMColors.SchwiftyPink
    "non-diegetic alternative reality" -> "🎬" to RnMColors.ToxicPurple
    "unknown", "" -> "❓" to RnMColors.SpaceBlack
    else -> "🌌" to RnMColors.ToxicPurple
}
