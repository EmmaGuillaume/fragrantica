package com.example.fragrantica

import com.example.fragrantica.data.PerfumeRepository
import com.example.fragrantica.data.PerfumeSearchParams
import com.example.fragrantica.ui.screens.CatalogueSortOption
import com.example.fragrantica.ui.screens.CatalogueUiState
import com.example.fragrantica.ui.screens.defaultMockupPerfumes
import com.example.fragrantica.ui.screens.resolvePerfumeDrawable
import com.example.fragrantica.ui.screens.toCardPerfume
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun catalogueMockupMapping_isCorrect() {
        assertEquals(6, defaultMockupPerfumes.size)
        val philosykos = defaultMockupPerfumes.first().toCardPerfume()
        assertEquals("Diptyque", philosykos.brand)
        assertEquals("Philosykos", philosykos.name)
        assertNotNull(resolvePerfumeDrawable("Philosykos"))
        assertNotNull(resolvePerfumeDrawable("Hiris"))
        assertNotNull(resolvePerfumeDrawable("Mandarine Basilic"))
    }

    @Test
    fun catalogueUiState_defaultsAndFiltersWork() {
        val defaultState = CatalogueUiState()
        assertEquals(CatalogueSortOption.Popularity, defaultState.sortOption)
        assertEquals(1, defaultState.currentPage)
        assertTrue(!defaultState.hasActiveFilters)

        val filteredState = defaultState.copy(
            activeQuery = "Hermès",
            selectedBrands = listOf("Hermès"),
            minRating4Plus = true,
        )
        assertTrue(filteredState.hasActiveFilters)
    }

    @Test
    fun supabaseRpc_searchPerfumes_whenConfigured() = runBlocking {
        if (BuildConfig.SUPABASE_URL == "null" || BuildConfig.SUPABASE_URL.isBlank()) {
            println("SUPABASE_URL non configuré dans local.properties — test RPC distant ignoré.")
            return@runBlocking
        }
        val repo = PerfumeRepository()
        val results = repo.searchPerfumesRpc(PerfumeSearchParams(limit = 6, offset = 0))
        println("RPC search_perfumes a retourné ${results.size} parfums (total=${results.firstOrNull()?.totalCount})")
        assertTrue(results.isNotEmpty())
    }
}