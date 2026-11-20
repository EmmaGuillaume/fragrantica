package com.example.fragrantica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragrantica.data.PerfumeDto

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Marque + nom
        Column {
            Text(formatDisplayName(p.brands.name), style = MaterialTheme.typography.titleMedium)
            Text(formatDisplayName(p.name), style = MaterialTheme.typography.headlineLarge)
        }

        // Infos générales : année · genre · pays
        val infos = listOfNotNull(
            p.releaseYear?.toString(),
            formatGenderOrConcentration(p.gender),
            p.brands.countries?.name
        )
        if (infos.isNotEmpty()) {
            Text(infos.joinToString(" · "), style = MaterialTheme.typography.bodyLarge)
        }

        // Note
        val rating = p.ratingValue
        if (rating != null) {
            Text(
                "★ ${"%.2f".format(rating)} / 5" +
                        (p.ratingCount?.let { " ($it avis)" } ?: ""),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Accords
        if (p.accords.isNotEmpty()) {
            Section("Accords") {
                Text(
                    p.accords.sortedBy { it.rank }.joinToString(" · ") { formatDisplayName(it.accords.name) },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Notes, regroupées par niveau (colonne "layer")
        if (p.notes.isNotEmpty()) {
            Section("Notes") {
                p.notes.groupBy { it.layer }.forEach { (layer, notes) ->
                    Text(
                        layer.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        notes.joinToString(", ") { formatDisplayName(it.notes.name) },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // Parfumeurs
        if (p.perfumers.isNotEmpty()) {
            Section("Parfumeur(s)") {
                Text(
                    p.perfumers.joinToString(", ") { formatDisplayName(it.perfumers.name) },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(onClick = { uriHandler.openUri(p.url) }) {
            Text("Voir sur Fragrantica")
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        content()
    }
}
