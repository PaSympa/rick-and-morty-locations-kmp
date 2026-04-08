package fr.leandremru.rickandmortylocations.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable error state with a branded retry button — shared by every screen that loads data.
 *
 * Uses [RnMButton] for the retry action so the design system stays consistent
 * across screens (same color, same typography, same padding).
 */
@Composable
fun RnMErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        RnMButton(text = "Retry", onClick = onRetry)
    }
}
