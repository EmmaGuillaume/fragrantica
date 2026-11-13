package com.example.fragrantica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fragrantica.ui.components.Perfume
import com.example.fragrantica.ui.components.PerfumeCard
import com.example.fragrantica.ui.components.PerfumeCardVariant
import com.fragrantica.ui.theme.Mandarine
import com.fragrantica.ui.theme.Texte

@Composable
fun ThemeTestScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Fragrantica", style = MaterialTheme.typography.headlineLarge)
        Text("Titre de section (Manrope)", style = MaterialTheme.typography.titleLarge)
        Text(
            "Texte courant en Inter : découvrez les parfums, leurs notes et leurs avis.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            "Texte secondaire",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Text(
                "Carte sur fond blanc",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Mandarine,
                contentColor = Texte,
            ),
        ) {
            Text(
                text = "Valider",
                fontWeight = FontWeight.Bold,
            )
        }
        val philosykos = Perfume(
            "Diptyque", "Philosykos", 1996, "Eau de toilette",
            4.18, 8200, listOf("Vert", "Boisé")
        )

        PerfumeCard(philosykos, variant = PerfumeCardVariant.Vertical)

        PerfumeCard(philosykos, variant = PerfumeCardVariant.Horizontal)
    }
}