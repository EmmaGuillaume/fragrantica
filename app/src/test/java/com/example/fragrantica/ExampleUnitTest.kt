package com.example.fragrantica

import com.example.fragrantica.data.PerfumeRepository
import com.example.fragrantica.data.PerfumeSearchParams
import com.example.fragrantica.ui.screens.CatalogueSortOption
import com.example.fragrantica.ui.screens.CatalogueUiState
import com.example.fragrantica.ui.screens.formatDisplayName
import com.example.fragrantica.ui.screens.resolvePerfumeDrawable
import com.example.fragrantica.ui.screens.toCardPerfume
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun displayFormatting_andDrawableMapping_isCorrect() {
        assertEquals("Hermès", formatDisplayName("hermes"))
        assertEquals("Terre d’Hermès", formatDisplayName("terre-d-hermes"))
        assertEquals("Un Jardin en Méditerranée", formatDisplayName("un-jardin-en-mediterranee"))
        assertEquals("Yves Saint Laurent", formatDisplayName("yves-saint-laurent"))
        assertNotNull(resolvePerfumeDrawable("philosykos-eau-de-parfum"))
        assertNotNull(resolvePerfumeDrawable("hiris"))
        assertNotNull(resolvePerfumeDrawable("aqua-allegoria-mandarine-basilic"))
    }

    @Test
    fun catalogueUiState_defaultsAndFiltersWork() {
        val defaultState = CatalogueUiState()
        assertEquals(CatalogueSortOption.Popularity, defaultState.sortOption)
        assertEquals(1, defaultState.currentPage)
        assertTrue(defaultState.perfumes.isEmpty())
        assertTrue(defaultState.selectedNotes.isEmpty())
        assertTrue(!defaultState.hasActiveFilters)

        val filteredState = defaultState.copy(
            activeQuery = "Hermès",
            selectedBrands = listOf("hermes"),
            minRating4Plus = true,
        )
        assertTrue(filteredState.hasActiveFilters)
    }

    @Test
    fun supabaseLive_searchAndFilters_returnRealDatabasePerfumes() = runBlocking {
        val repo = PerfumeRepository()

        // 1. Chargement initial du catalogue complet (sans filtre)
        val initialPage = repo.searchPerfumesRpc(PerfumeSearchParams(limit = 6, offset = 0))
        println("Page 1 (sans filtre): ${initialPage.map { "${it.brandName} - ${it.name} (${it.ratingCount} avis)" }} | total=${initialPage.firstOrNull()?.totalCount}")
        assertEquals(6, initialPage.size)
        assertTrue((initialPage.first().totalCount) > 20_000L)

        // 2. Chargement dynamique des options de filtres (marques, accords, notes)
        val filterOptions = repo.getCatalogueFilterOptions()
        println("Options de filtres Supabase: brands=${filterOptions.brands.take(5)}, accords=${filterOptions.accords.take(5)}, notes=${filterOptions.notes.take(5)}")
        assertTrue(filterOptions.brands.isNotEmpty())
        assertTrue(filterOptions.accords.isNotEmpty())
        assertTrue(filterOptions.notes.isNotEmpty())

        // 3. Recherche textuelle par nom de parfum avec accents / apostrophes ("Terre d'Hermès")
        val terreResults = repo.searchPerfumesRpc(PerfumeSearchParams(query = "Terre d'Hermès"))
        println("Recherche 'Terre d'Hermès': ${terreResults.size} résultats (total=${terreResults.firstOrNull()?.totalCount})")
        assertTrue(terreResults.isNotEmpty())
        assertTrue(terreResults.any { "terre-d-hermes" in it.name })

        // 4. Recherche textuelle par note olfactive en français ("Feuille de figuier")
        val figLeafResults = repo.searchPerfumesRpc(PerfumeSearchParams(query = "Feuille de figuier"))
        println("Recherche 'Feuille de figuier': total=${figLeafResults.firstOrNull()?.totalCount}, top=${figLeafResults.map { it.name }}")
        assertTrue(figLeafResults.isNotEmpty())
        assertTrue(figLeafResults.any { "philosykos" in it.name })

        // 5. Filtrage combiné : Marque (Hermès) + Genre (women) + Note minimale >= 4.0
        val hermesWomen4Plus = repo.searchPerfumesRpc(
            PerfumeSearchParams(
                gender = "women",
                brands = listOf("Hermès"),
                minRating = 4.0,
            )
        )
        println("Filtre Hermès + Femme + 4+: total=${hermesWomen4Plus.firstOrNull()?.totalCount}, résultats=${hermesWomen4Plus.map { "${it.name} (${it.ratingValue})" }}")
        assertTrue(hermesWomen4Plus.isNotEmpty())
        assertTrue(hermesWomen4Plus.all { it.brandName == "hermes" && it.gender == "women" && (it.ratingValue ?: 0.0) >= 4.0 })

        // 6. Filtrage par accord + note + plage d'années
        val woodyBergamotRecent = repo.searchPerfumesRpc(
            PerfumeSearchParams(
                accords = listOf("woody"),
                notes = listOf("bergamot"),
                yearMin = 2015,
                yearMax = 2024,
                sort = "recent",
            )
        )
        println("Filtre woody + bergamot + 2015..2024: total=${woodyBergamotRecent.firstOrNull()?.totalCount}")
        assertTrue(woodyBergamotRecent.isNotEmpty())
        assertTrue(woodyBergamotRecent.all { (it.releaseYear ?: 0) in 2015..2024 })

        // 7. Autocomplétion RPC search_names
        val brandSuggestions = repo.searchNamesRpc("brand", "Hermès")
        val noteSuggestions = repo.searchNamesRpc("note", "Feuille de figuier")
        println("Suggestions marque 'Hermès' -> $brandSuggestions | note 'Feuille de figuier' -> $noteSuggestions")
        assertTrue(brandSuggestions.any { it == "hermes" })
        assertTrue(noteSuggestions.any { it == "fig leaf" })

        // 8. Mapping vers PerfumeCard
        val card = terreResults.first().toCardPerfume()
        assertEquals("Hermès", card.brand)
    }
}
