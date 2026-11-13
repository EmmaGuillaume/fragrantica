package com.example.fragrantica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.fragrantica.ui.theme.FragranticaTheme
import com.fragrantica.ui.theme.Mandarine
import com.fragrantica.ui.theme.Texte

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FragranticaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ThemeTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ThemeTestScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
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
        }    }
}

@Preview(showBackground = true)
@Composable
fun ThemeTestScreenPreview() {
    FragranticaTheme {
        ThemeTestScreen()
    }
}