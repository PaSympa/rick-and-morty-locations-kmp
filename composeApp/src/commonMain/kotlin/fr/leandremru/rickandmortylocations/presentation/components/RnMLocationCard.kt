package fr.leandremru.rickandmortylocations.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.leandremru.rickandmortylocations.domain.model.Location

/** Reusable card showing a location summary. Used in the list and master pane. */
@Composable
fun RnMLocationCard(
    location: Location,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = location.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${location.type} · ${location.dimension}",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "${location.residentCount} residents",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
