package com.example.fragrantica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AProposScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("À propos", style = MaterialTheme.typography.headlineMedium)
    }
}