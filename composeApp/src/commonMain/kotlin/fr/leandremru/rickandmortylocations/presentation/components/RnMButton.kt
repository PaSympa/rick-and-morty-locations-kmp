package fr.leandremru.rickandmortylocations.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Branded primary button for the Rick & Morty Locations app.
 *
 * Wraps Material 3 [Button] with the brand color scheme (portal green) and the
 * design-system typography. Every actionable button in the app should use this
 * composable rather than [Button] directly, so a brand-wide tweak only requires
 * editing one file.
 */
@Composable
fun RnMButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
