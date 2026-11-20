package com.example.fragrantica.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragrantica.data.PerfumeRepository
import com.example.fragrantica.data.PerfumeSummaryDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AccueilState {
    data object Loading : AccueilState
    data class Error(val message: String) : AccueilState
    data class Success(val perfumes: List<PerfumeSummaryDto>) : AccueilState
}

class AccueilViewModel(
    private val repo: PerfumeRepository = PerfumeRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<AccueilState>(AccueilState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = AccueilState.Loading
            _state.value = try {
                AccueilState.Success(repo.getHomePerfumes(10))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Supabase", "Erreur chargement accueil", e)
                AccueilState.Error("Impossible de charger les parfums.")
            }
        }
    }
}