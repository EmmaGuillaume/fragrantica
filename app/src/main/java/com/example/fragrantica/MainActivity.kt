package com.example.fragrantica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.fragrantica.ui.components.Perfume
import com.example.fragrantica.ui.components.PerfumeCard
import com.example.fragrantica.ui.components.PerfumeCardVariant
import com.fragrantica.ui.theme.FragranticaTheme
import com.fragrantica.ui.theme.Mandarine
import com.fragrantica.ui.theme.Texte
import com.example.fragrantica.navigation.AppNavHost
import com.example.fragrantica.ui.components.Header

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FragranticaTheme() {
                val navController = rememberNavController()
                Scaffold(topBar = { Header(navController) }) { padding ->
                    AppNavHost(navController, Modifier.padding(padding))
                }
            }
        }
    }
}
@Composable
fun ThemeTestScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Fragrantica", style = MaterialTheme.typography.displayLarge)
        Text("Titre de section (Manrope)", style = MaterialTheme.typography.displayLarge)
        Text(
            "Texte courant en Inter : découvrez les parfums, leurs notes et leurs avis.",
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            "Texte secondaire",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Text(
                "Carte sur fond blanc",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.displayLarge,
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

@Preview(showBackground = true)
@Composable
fun ThemeTestScreenPreview() {
    FragranticaTheme {
        ThemeTestScreen()
    }
}