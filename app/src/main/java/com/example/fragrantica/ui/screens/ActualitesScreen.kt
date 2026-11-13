package com.example.fragrantica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActualitesScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Actulites", style = MaterialTheme.typography.headlineMedium)
    }
}