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

const val DEFAULT_YEAR_MIN = 1990
const val DEFAULT_YEAR_MAX = 2026

data class CatalogueUiState(
    val queryInput: String = "",
    val activeQuery: String = "",
    val selectedGender: String? = null, // null = Tous, "women" = Femme, "men" = Homme, "unisex" = Mixte
    val yearMin: Int = DEFAULT_YEAR_MIN,
    val yearMax: Int = DEFAULT_YEAR_MAX,
    val yearFilterActive: Boolean = false,
    val availableBrands: List<String> = emptyList(),
    val selectedBrands: List<String> = emptyList(),
    val brandSearchInput: String = "",
    val brandSuggestions: List<String> = emptyList(),
    val availableNotes: List<String> = emptyList(),
    val selectedNotes: List<String> = emptyList(),
    val noteSearchInput: String = "",
    val noteSuggestions: List<String> = emptyList(),
    val availableAccords: List<String> = emptyList(),
    val selectedAccords: List<String> = emptyList(),
    val accordSearchInput: String = "",
    val accordSuggestions: List<String> = emptyList(),
    val minRating4Plus: Boolean = false,
    val sortOption: CatalogueSortOption = CatalogueSortOption.Popularity,
    val currentPage: Int = 1,
    val pageSize: Int = 6,
    val totalCount: Long = 0L,
    val perfumes: List<SearchPerfumeRpcDto> = emptyList(),
    val isLoading: Boolean = true,
    val isFiltersLoading: Boolean = true,
    val error: String? = null,
) {
    val totalPages: Int
        get() = if (totalCount <= 0L) 1 else ((totalCount + pageSize - 1) / pageSize).toInt()

    val hasActiveFilters: Boolean
        get() = activeQuery.isNotBlank() ||
            selectedGender != null ||
            selectedBrands.isNotEmpty() ||
            selectedNotes.isNotEmpty() ||
            selectedAccords.isNotEmpty() ||
            minRating4Plus ||
            yearFilterActive
}

class CatalogueViewModel(
    private val repo: PerfumeRepository = PerfumeRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogueUiState())
    val state: StateFlow<CatalogueUiState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var queryDebounceJob: Job? = null
    private var brandAutocompleteJob: Job? = null
    private var noteAutocompleteJob: Job? = null
    private var accordAutocompleteJob: Job? = null

    init {
        loadFilterOptions()
        executeSearch()
    }

    fun loadFilterOptions() {
        viewModelScope.launch {
            _state.update { it.copy(isFiltersLoading = true) }
            try {
                val options = repo.getCatalogueFilterOptions()
                _state.update { current ->
                    val mergedBrands = (current.selectedBrands + options.brands)
                        .distinctBy { it.lowercase() }
                    val mergedAccords = (current.selectedAccords + options.accords)
                        .distinctBy { it.lowercase() }
                    current.copy(
                        availableBrands = mergedBrands,
                        availableAccords = mergedAccords,
                        availableNotes = options.notes,
                        isFiltersLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Supabase", "Erreur lors du chargement des filtres catalogue", e)
                _state.update { it.copy(isFiltersLoading = false) }
            }
        }
    }

    fun applyInitialFilters(
        query: String? = null,
        brand: String? = null,
        accord: String? = null,
        note: String? = null,
    ) {
        val hasAny = !query.isNullOrBlank() ||
            !brand.isNullOrBlank() ||
            !accord.isNullOrBlank() ||
            !note.isNullOrBlank()
        if (!hasAny) return

        queryDebounceJob?.cancel()
        _state.update { current ->
            val nextQuery = query?.trim().orEmpty()
            val nextBrands = if (!brand.isNullOrBlank()) listOf(brand.trim()) else emptyList()
            val nextAccords = if (!accord.isNullOrBlank()) listOf(accord.trim()) else emptyList()
            val nextNotes = if (!note.isNullOrBlank()) listOf(note.trim()) else emptyList()

            current.copy(
                queryInput = nextQuery,
                activeQuery = nextQuery,
                selectedGender = null,
                selectedBrands = nextBrands,
                availableBrands = (nextBrands + current.availableBrands).distinctBy { it.lowercase() },
                selectedAccords = nextAccords,
                availableAccords = (nextAccords + current.availableAccords).distinctBy { it.lowercase() },
                selectedNotes = nextNotes,
                minRating4Plus = false,
                yearFilterActive = false,
                yearMin = DEFAULT_YEAR_MIN,
                yearMax = DEFAULT_YEAR_MAX,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onQueryInputChange(query: String) {
        _state.update { it.copy(queryInput = query) }
        queryDebounceJob?.cancel()
        queryDebounceJob = viewModelScope.launch {
            delay(350)
            val trimmed = query.trim()
            if (trimmed != _state.value.activeQuery) {
                _state.update {
                    it.copy(
                        activeQuery = trimmed,
                        currentPage = 1,
                    )
                }
                executeSearch()
            }
        }
    }

    fun submitSearch() {
        queryDebounceJob?.cancel()
        _state.update {
            it.copy(
                activeQuery = it.queryInput.trim(),
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun clearQuery() {
        queryDebounceJob?.cancel()
        _state.update {
            it.copy(
                queryInput = "",
                activeQuery = "",
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onSelectGender(gender: String?) {
        _state.update { current ->
            val nextGender = if (gender != null && current.selectedGender == gender) null else gender
            current.copy(
                selectedGender = nextGender,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onYearRangeChange(minYear: Int, maxYear: Int) {
        val coercedMin = minYear.coerceIn(1700, DEFAULT_YEAR_MAX)
        val coercedMax = maxYear.coerceIn(coercedMin, DEFAULT_YEAR_MAX)
        _state.update {
            it.copy(
                yearMin = coercedMin,
                yearMax = coercedMax,
                yearFilterActive = true,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun resetYearRange() {
        _state.update {
            it.copy(
                yearMin = DEFAULT_YEAR_MIN,
                yearMax = DEFAULT_YEAR_MAX,
                yearFilterActive = false,
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onToggleBrand(brand: String) {
        val cleaned = brand.trim()
        if (cleaned.isEmpty()) return
        _state.update { current ->
            val updated = if (current.selectedBrands.any { it.equals(cleaned, ignoreCase = true) }) {
                current.selectedBrands.filterNot { it.equals(cleaned, ignoreCase = true) }
            } else {
                current.selectedBrands + cleaned
            }
            val updatedAvailable = (updated + current.availableBrands).distinctBy { it.lowercase() }
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
            val remote = runCatching { repo.searchNamesRpc("brand", input.trim(), limit = 8) }
                .getOrDefault(emptyList())
            _state.update { it.copy(brandSuggestions = remote) }
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
            _state.update { current ->
                current.copy(
                    noteSuggestions = current.availableNotes
                        .filter { n -> current.selectedNotes.none { it.equals(n, ignoreCase = true) } }
                        .take(6)
                )
            }
            return
        }
        noteAutocompleteJob = viewModelScope.launch {
            delay(250)
            val remote = runCatching { repo.searchNamesRpc("note", input.trim(), limit = 8) }
                .getOrDefault(emptyList())
            _state.update { current ->
                current.copy(
                    noteSuggestions = remote.filter { n ->
                        current.selectedNotes.none { it.equals(n, ignoreCase = true) }
                    }
                )
            }
        }
    }

    fun onToggleAccord(accord: String) {
        val cleaned = accord.trim()
        if (cleaned.isEmpty()) return
        _state.update { current ->
            val updated = if (current.selectedAccords.any { it.equals(cleaned, ignoreCase = true) }) {
                current.selectedAccords.filterNot { it.equals(cleaned, ignoreCase = true) }
            } else {
                current.selectedAccords + cleaned
            }
            val updatedAvailable = (updated + current.availableAccords).distinctBy { it.lowercase() }
            current.copy(
                selectedAccords = updated,
                availableAccords = updatedAvailable,
                accordSearchInput = "",
                accordSuggestions = emptyList(),
                currentPage = 1,
            )
        }
        executeSearch()
    }

    fun onAccordSearchInputChange(input: String) {
        _state.update { it.copy(accordSearchInput = input) }
        accordAutocompleteJob?.cancel()
        if (input.isBlank()) {
            _state.update { it.copy(accordSuggestions = emptyList()) }
            return
        }
        accordAutocompleteJob = viewModelScope.launch {
            delay(200)
            val remote = runCatching { repo.searchNamesRpc("accord", input.trim(), limit = 8) }
                .getOrDefault(emptyList())
            _state.update { it.copy(accordSuggestions = remote) }
        }
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
        val s = _state.value
        val target = page.coerceIn(1, s.totalPages)
        if (target == s.currentPage) return
        _state.update { it.copy(currentPage = target) }
        executeSearch()
    }

    fun resetFilters() {
        queryDebounceJob?.cancel()
        _state.update { current ->
            CatalogueUiState(
                availableBrands = current.availableBrands,
                availableAccords = current.availableAccords,
                availableNotes = current.availableNotes,
                isFiltersLoading = current.isFiltersLoading,
            )
        }
        executeSearch()
    }

    fun executeSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true, error = null) }

            val params = PerfumeSearchParams(
                query = s.activeQuery,
                gender = s.selectedGender,
                brands = s.selectedBrands,
                accords = s.selectedAccords,
                notes = s.selectedNotes,
                yearMin = if (s.yearFilterActive) s.yearMin else null,
                yearMax = if (s.yearFilterActive) s.yearMax else null,
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
                Log.e("Supabase", "Erreur lors de la recherche de parfums sur Supabase", e)
                _state.update {
                    it.copy(
                        perfumes = emptyList(),
                        totalCount = 0L,
                        isLoading = false,
                        error = "Impossible de charger les parfums depuis la base de données (${e.message ?: "erreur réseau"}).",
                    )
                }
            }
        }
    }
}
