package com.example.fragrantica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragrantica.data.PerfumeDto
import com.example.fragrantica.model.DetailState
import com.example.fragrantica.model.PerfumeDetailViewModel

@Composable
fun PerfumeDetailScreen(
    onBack: () -> Unit,
    vm: PerfumeDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text("← Retour")
        }

        when (val s = state) {
            DetailState.Loading -> Centered { CircularProgressIndicator() }

            DetailState.NotFound -> Centered { Text("Parfum introuvable.") }

            is DetailState.Error -> Centered {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = vm::load) { Text("Réessayer") }
                }
            }

            is DetailState.Success -> PerfumeDetailContent(s.perfume)
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
private fun PerfumeDetailContent(p: PerfumeDto) {
    val uriHandler = LocalUriHandler.current
    val colors = MaterialTheme.colorScheme

    // Infos générales : année · genre · pays
    val infos = listOfNotNull(
        p.releaseYear?.toString(),
        formatGenderOrConcentration(p.gender),
        p.brands.countries?.name
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
    ) {

        // IDENTITÉ DU PARFUM
        Column(
            modifier = Modifier.padding(
                horizontal = 22.dp,
                vertical = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                formatDisplayName(p.brands.name).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = colors.primary
            )

            Text(
                formatDisplayName(p.name),
                style = MaterialTheme.typography.headlineLarge,
                color = colors.secondary
            )

            if (infos.isNotEmpty()) {
                Text(
                    infos.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            // NOTE
            p.ratingValue?.let { rating ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("★", color = Color(0xFFF28C28))

                    Text(
                        "%.2f / 5".format(rating),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.secondary
                    )

                    p.ratingCount?.let {
                        Text(
                            "· $it avis",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ACCORDS PRINCIPAUX
        if (p.accords.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Accords principaux",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.secondary
                )

                val accordColors = listOf(
                    Color(0xFFE6EEE2),
                    Color(0xFFDAC9B3),
                    Color(0xFFF28C28),
                    Color(0xFFE7E2D8),
                    Color(0xFFBFD9EE)
                )

                p.accords.sortedBy { it.rank }
                    .forEachIndexed { index, accord ->

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    0.72f + (0.28f * (p.accords.size - index) / p.accords.size)
                                )
                                .background(
                                    accordColors[index % accordColors.size],
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 7.dp
                                )
                        ) {
                            Text(
                                formatDisplayName(accord.accords.name),
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.secondary
                            )
                        }
                    }
            }
        }

        Spacer(Modifier.height(24.dp))

        // PYRAMIDE OLFACTIVE (notes regroupées par niveau, colonne "layer")
        if (p.notes.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "La pyramide olfactive",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.secondary
                )

                Text(
                    "Découvrez les notes qui composent ce parfum.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                val noteColors = listOf(
                    Color(0xFFE6EEE2),
                    Color(0xFFBFD9EE),
                    Color(0xFFDAC9B3)
                )

                p.notes
                    .groupBy { it.layer }
                    .entries
                    .forEachIndexed { index, (layer, notes) ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    noteColors[index % noteColors.size],
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                layer.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.secondary
                            )

                            Text(
                                notes.joinToString(" · ") { formatDisplayName(it.notes.name) },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onSurface
                            )
                        }
                    }
            }
        }

        // PARFUMEURS
        if (p.perfumers.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 22.dp,
                    vertical = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "La création",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.secondary
                )

                Text(
                    "Parfumeur(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.primary
                )

                Text(
                    p.perfumers.joinToString(", ") { formatDisplayName(it.perfumers.name) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
            }
        }

        // LIEN FRAGRANTICA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 30.dp)
        ) {
            Button(
                onClick = { uriHandler.openUri(p.url) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                )
            ) {
                Text("Voir sur Fragrantica")
            }
        }
    }
}