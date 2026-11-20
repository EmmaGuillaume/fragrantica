package com.example.fragrantica.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragrantica.data.PerfumeRepository
import com.example.fragrantica.data.PerfumeSearchParams
import com.example.fragrantica.data.SearchPerfumeRpcDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CatalogueSortOption(val key: String, val label: String) {
    Popularity("popularity", "Popularité ↓"),
    Rating("rating", "Note ↓"),
    Recent("recent", "Plus récents ↓"),
    Name("name", "Nom A–Z"),
}

data class CatalogueUiState(
    val queryInput: String = "",
    val activeQuery: String = "",
    val selectedGender: String? = null, // null = Tous, "women" = Femme, "men" = Homme, "unisex" = Mixte
    val yearMin: Int = 1990,
    val yearMax: Int = 2026,
    val availableBrands: List<String> = listOf("Diptyque", "Hermès", "Le Labo", "Guerlain"),
    val selectedBrands: List<String> = emptyList(),
    val brandSearchInput: String = "",
    val brandSuggestions: List<String> = emptyList(),
    val selectedNotes: List<String> = listOf("Feuille de figuier"),
    val noteSearchInput: String = "",
    val noteSuggestions: List<String> = emptyList(),
    val availableAccords: List<String> = listOf("Vert", "Boisé", "Agrumes", "Aquatique", "Floral", "Épicé"),
    val selectedAccords: List<String> = emptyList(),
    val minRating4Plus: Boolean = false,
    val sortOption: CatalogueSortOption = CatalogueSortOption.Popularity,
    val currentPage: Int = 1,
    val pageSize: Int = 6,
    val totalCount: Long = 6L,
    val perfumes: List<SearchPerfumeRpcDto> = defaultMockupPerfumes,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val totalPages: Int
        get() = maxOf(3, ((totalCount + pageSize - 1) / pageSize).toInt())

    val hasActiveFilters: Boolean
        get() = activeQuery.isNotBlank() ||
            selectedGender != null ||
            selectedBrands.isNotEmpty() ||
            selectedAccords.isNotEmpty() ||
            minRating4Plus ||
            yearMin != 1990 ||
            yearMax != 2026
}

val defaultMockupPerfumes = listOf(
    SearchPerfumeRpcDto(
        id = 1,
        name = "Philosykos",
        brandName = "Diptyque",
        gender = "Eau de toilette",
        releaseYear = 1996,
        ratingValue = 4.18,
        ratingCount = 8624,
        accords = listOf("Vert", "Boisé"),
        totalCount = 6L,
    ),
    SearchPerfumeRpcDto(
        id = 2,
        name = "Un Jardin en Méditerranée",
        brandName = "Hermès",
        gender = "Eau de toilette",
        releaseYear = 2003,
        ratingValue = 4.12,
        ratingCount = 5218,
        accords = listOf("Vert", "Aquatique"),
        totalCount = 6L,
    ),
    SearchPerfumeRpcDto(
        id = 3,
        name = "Terre d’Hermès",
        brandName = "Hermès",
        gender = "Eau de toilette",
        releaseYear = 2006,
        ratingValue = 4.30,
        ratingCount = 12480,
        accords = listOf("Boisé", "Agrumes"),
        totalCount = 6L,
    ),
    SearchPerfumeRpcDto(
        id = 4,
        name = "Santal 33",
        brandName = "Le Labo",
        gender = "Eau de parfum",
        releaseYear = 2011,
        ratingValue = 3.92,
        ratingCount = 6410,
        accords = listOf("Boisé", "Épicé"),
        totalCount = 6L,
    ),
    SearchPerfumeRpcDto(
        id = 5,
        name = "Hiris",
        brandName = "Hermès",
        gender = "Eau de toilette",
        releaseYear = 1999,
        ratingValue = 4.02,
        ratingCount = 3420,
        accords = listOf("Floral", "Poudré"),
        totalCount = 6L,
    ),
    SearchPerfumeRpcDto(
        id = 6,
        name = "Mandarine Basilic",
        brandName = "Guerlain",
        gender = "Eau de toilette",
        releaseYear = 2007,
        ratingValue = 4.10,
        ratingCount = 7150,
        accords = listOf("Agrumes", "Aromatique"),
        totalCount = 6L,
    ),
)

class CatalogueViewModel(
    private val repo: PerfumeRepository = PerfumeRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogueUiState())
    val state: StateFlow<CatalogueUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var brandAutocompleteJob: Job? = null
    private var noteAutocompleteJob: Job? = null

    init {
        executeSearch()
    }

    fun onQueryInputChange(query: String) {
        _state.update { it.copy(queryInput = query) }
    }

    fun submitSearch() {
        _state.update {
            it.copy(
                activeQuery = it.queryInput.trim(),
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onSelectGender(gender: String?) {
        _state.update {
            it.copy(
                selectedGender = gender,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onYearRangeChange(minYear: Int, maxYear: Int) {
        _state.update {
            it.copy(
                yearMin = minYear.coerceIn(1900, 2026),
                yearMax = maxYear.coerceIn(1900, 2026),
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onToggleBrand(brand: String) {
        _state.update { current ->
            val updated = if (current.selectedBrands.any { it.equals(brand, ignoreCase = true) }) {
                current.selectedBrands.filterNot { it.equals(brand, ignoreCase = true) }
            } else {
                current.selectedBrands + brand
            }
            val updatedAvailable = if (current.availableBrands.any { it.equals(brand, ignoreCase = true) }) {
                current.availableBrands
            } else {
                listOf(brand) + current.availableBrands
            }
            current.copy(
                selectedBrands = updated,
                availableBrands = updatedAvailable,
                brandSearchInput = "",
                brandSuggestions = emptyList(),
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onBrandSearchInputChange(input: String) {
        _state.update { it.copy(brandSearchInput = input) }
        brandAutocompleteJob?.cancel()
        if (input.isBlank()) {
            _state.update { it.copy(brandSuggestions = emptyList()) }
            return
        }
        brandAutocompleteJob = viewModelScope.launch {
            delay(250)
            val remote = runCatching { repo.searchNamesRpc("brand", input.trim()) }
                .getOrDefault(emptyList())
            val local = _state.value.availableBrands.filter {
                it.contains(input.trim(), ignoreCase = true)
            }
            val merged = (remote + local).distinctBy { it.lowercase() }.take(6)
            _state.update { it.copy(brandSuggestions = merged) }
        }
    }

    fun onAddNote(note: String) {
        val cleaned = note.trim()
        if (cleaned.isEmpty()) return
        _state.update { current ->
            if (current.selectedNotes.any { it.equals(cleaned, ignoreCase = true) }) {
                current.copy(noteSearchInput = "", noteSuggestions = emptyList())
            } else {
                current.copy(
                    selectedNotes = current.selectedNotes + cleaned,
                    noteSearchInput = "",
                    noteSuggestions = emptyList(),
                    currentPage = 1,
                )
            }
        }
        executeSearch()
    }

    fun onRemoveNote(note: String) {
        _state.update { current ->
            current.copy(
                selectedNotes = current.selectedNotes.filterNot { it.equals(note, ignoreCase = true) },
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onNoteSearchInputChange(input: String) {
        _state.update { it.copy(noteSearchInput = input) }
        noteAutocompleteJob?.cancel()
        if (input.isBlank()) {
            _state.update { it.copy(noteSuggestions = emptyList()) }
            return
        }
        noteAutocompleteJob = viewModelScope.launch {
            delay(250)
            val remote = runCatching { repo.searchNamesRpc("note", input.trim()) }
                .getOrDefault(emptyList())
            val fallbackNotes = listOf(
                "Feuille de figuier",
                "Mandarine",
                "Santal",
                "Iris",
                "Cèdre",
                "Bergamote",
                "Vétiver",
                "Fleur d’oranger",
            ).filter { it.contains(input.trim(), ignoreCase = true) }
            val merged = (remote + fallbackNotes).distinctBy { it.lowercase() }.take(6)
            _state.update { it.copy(noteSuggestions = merged) }
        }
    }

    fun onToggleAccord(accord: String) {
        _state.update { current ->
            val updated = if (current.selectedAccords.any { it.equals(accord, ignoreCase = true) }) {
                current.selectedAccords.filterNot { it.equals(accord, ignoreCase = true) }
            } else {
                current.selectedAccords + accord
            }
            current.copy(
                selectedAccords = updated,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onToggleMinRating4Plus() {
        _state.update {
            it.copy(
                minRating4Plus = !it.minRating4Plus,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onSelectSort(sortOption: CatalogueSortOption) {
        _state.update {
            it.copy(
                sortOption = sortOption,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onSelectPage(page: Int) {
        val target = page.coerceAtLeast(1)
        if (target == _state.value.currentPage) return
        _state.update { it.copy(currentPage = target) }
        executeSearch()
    }

    fun resetFilters() {
        _state.update {
            CatalogueUiState(
                availableBrands = it.availableBrands,
                availableAccords = it.availableAccords,
                selectedNotes = emptyList(),
            )
        }
        executeSearch()
    }

    fun executeSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, error = null) }

            val activeNotesForRpc = if (s.selectedNotes == listOf("Feuille de figuier") && !s.hasActiveFilters) {
                // Par défaut sur la maquette, "Feuille de figuier" est affiché à titre d'exemple tout en montrant les 6 parfums
                emptyList()
            } else {
                s.selectedNotes
            }

            val params = PerfumeSearchParams(
                query = s.activeQuery,
                gender = s.selectedGender,
                brands = s.selectedBrands,
                accords = s.selectedAccords,
                notes = activeNotesForRpc,
                yearMin = s.yearMin,
                yearMax = s.yearMax,
                minRating = if (s.minRating4Plus) 4.0 else null,
                sort = s.sortOption.key,
                limit = s.pageSize,
                offset = (s.currentPage - 1) * s.pageSize,
            )

            try {
                val results = repo.searchPerfumesRpc(params)
                val total = results.firstOrNull()?.totalCount ?: 0L
                _state.update {
                    it.copy(
                        perfumes = results,
                        totalCount = total,
                        isLoading = false,
                        error = null,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("Supabase", "RPC search_perfumes indisponible, utilisation du catalogue local de secours", e)
                val fallback = filterLocalMockupPerfumes(s)
                _state.update {
                    it.copy(
                        perfumes = fallback,
                        totalCount = fallback.size.toLong(),
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun filterLocalMockupPerfumes(s: CatalogueUiState): List<SearchPerfumeRpcDto> {
        val q = s.activeQuery.trim().lowercase()
        val filtered = defaultMockupPerfumes.filter { p ->
            val matchesQuery = q.isEmpty() ||
                p.name.lowercase().contains(q) ||
                p.brandName.lowercase().contains(q) ||
                p.accords.any { it.lowercase().contains(q) }

            val matchesBrand = s.selectedBrands.isEmpty() ||
                s.selectedBrands.any { it.equals(p.brandName, ignoreCase = true) }

            val matchesAccord = s.selectedAccords.isEmpty() ||
                s.selectedAccords.any { sel -> p.accords.any { it.equals(sel, ignoreCase = true) } }

            val matchesYear = (p.releaseYear == null) ||
                (p.releaseYear in s.yearMin..s.yearMax)

            val matchesRating = !s.minRating4Plus ||
                ((p.ratingValue ?: 0.0) >= 4.0)

            matchesQuery && matchesBrand && matchesAccord && matchesYear && matchesRating
        }

        return when (s.sortOption) {
            CatalogueSortOption.Popularity -> filtered.sortedByDescending { it.ratingCount ?: 0 }
            CatalogueSortOption.Rating -> filtered.sortedByDescending { it.ratingValue ?: 0.0 }
            CatalogueSortOption.Recent -> filtered.sortedByDescending { it.releaseYear ?: 0 }
            CatalogueSortOption.Name -> filtered.sortedBy { it.name.lowercase() }
        }
    }
}
