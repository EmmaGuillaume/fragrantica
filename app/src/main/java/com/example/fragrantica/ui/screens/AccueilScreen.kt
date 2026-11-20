package com.example.fragrantica.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragrantica.data.PerfumeDto
import com.example.fragrantica.data.PerfumeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun AccueilScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Accueil", style = MaterialTheme.typography.headlineMedium)
    }
}
class ParfumsViewModel(private val repo: PerfumeRepository = PerfumeRepository()) : ViewModel() {
    private val _perfumes = MutableStateFlow<List<PerfumeDto>>(emptyList())
    val perfumes = _perfumes.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { repo.topPerfumes(20) }
                .onSuccess { _perfumes.value = it }
                .onFailure { Log.e("Supabase", "Erreur chargement", it) }
        }
    }
}