package com.example.fragrantica.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragrantica.data.PerfumeDto
import com.example.fragrantica.data.PerfumeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailState {
    data object Loading : DetailState
    data object NotFound : DetailState
    data class Error(val message: String) : DetailState
    data class Success(val perfume: PerfumeDto) : DetailState
}

class PerfumeDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repo = PerfumeRepository()

    // L'argument "perfumeId" de la route detail/{perfumeId} arrive ici automatiquement
    private val perfumeId: Int = checkNotNull(savedStateHandle.get<Int>("perfumeId"))

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            _state.value = try {
                repo.getPerfumeById(perfumeId)
                    ?.let { DetailState.Success(it) }
                    ?: DetailState.NotFound
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Supabase", "Erreur détail parfum", e)
                DetailState.Error("Impossible de charger ce parfum.")
            }
        }
    }
}