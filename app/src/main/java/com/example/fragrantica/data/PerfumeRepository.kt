package com.example.fragrantica.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.Normalizer

data class PerfumeSearchParams(
    val query: String = "",
    val gender: String? = null,
    val brands: List<String> = emptyList(),
    val accords: List<String> = emptyList(),
    val notes: List<String> = emptyList(),
    val yearMin: Int? = null,
    val yearMax: Int? = null,
    val minRating: Double? = null,
    val sort: String = "popularity",
    val limit: Int = 6,
    val offset: Int = 0,
)

data class CatalogueFilterOptions(
    val brands: List<String>,
    val accords: List<String>,
    val notes: List<String>,
    val minYear: Int = 1781,
    val maxYear: Int = 2026,
)

fun normalizeSearchSlug(input: String): String {
    val decomposed = Normalizer.normalize(input.trim().lowercase(), Normalizer.Form.NFD)
    val withoutAccents = decomposed
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .replace("œ", "oe")
        .replace("æ", "ae")
    return withoutAccents
        .replace(Regex("[^a-z0-9]+"), "-")
        .trim('-')
}

private val frenchToEnglishOlfactiveMap = mapOf(
    // Accords
    "vert" to "green",
    "boise" to "woody",
    "agrumes" to "citrus",
    "aquatique" to "aquatic",
    "floral" to "floral",
    "epice" to "spicy",
    "chaud-epice" to "warm spicy",
    "frais-epice" to "fresh spicy",
    "doux-epice" to "soft spicy",
    "poudre" to "powdery",
    "aromatique" to "aromatic",
    "gourmand" to "gourmand",
    "sucre" to "sweet",
    "fruite" to "fruity",
    "ambre" to "amber",
    "vanille" to "vanilla",
    "musque" to "musky",
    "fleurs-blanches" to "white floral",
    "floral-blanc" to "white floral",
    "floral-jaune" to "yellow floral",
    "cuir" to "leather",
    "fume" to "smoky",
    "terreux" to "earthy",
    "marin" to "marine",
    "frais" to "fresh",
    "lacte" to "lactonic",
    "savonneux" to "soapy",
    "animal" to "animalic",
    "miel" to "honey",
    "tabac" to "tobacco",
    "cafe" to "coffee",
    "cannelle" to "cinnamon",
    "amande" to "almond",
    "cerise" to "cherry",
    "chocolat" to "chocolate",
    "noix-de-coco" to "coconut",
    "coco" to "coconut",
    "violette" to "violet",
    // Notes
    "feuille-de-figuier" to "fig leaf",
    "figuier" to "fig tree",
    "figue" to "fig",
    "mandarine" to "mandarin orange",
    "santal" to "sandalwood",
    "bois-de-santal" to "sandalwood",
    "cedre" to "cedar",
    "bois-de-cedre" to "cedar",
    "bergamote" to "bergamot",
    "vetiver" to "vetiver",
    "fleur-d-oranger" to "orange blossom",
    "sel-marin" to "sea salt",
    "sel" to "salt",
    "jasmin" to "jasmine",
    "lavande" to "lavender",
    "citron" to "lemon",
    "feve-tonka" to "tonka bean",
    "tonka" to "tonka bean",
    "musc" to "musk",
)

fun translateOlfactiveTerm(input: String): String {
    val slug = normalizeSearchSlug(input)
    return frenchToEnglishOlfactiveMap[slug] ?: input.trim().lowercase()
}

class PerfumeRepository {
    private val db = SupabaseProvider.client.postgrest

    // Requête légère : pour les listes (accueil)
    private val listColumns = Columns.raw(
        """
        id, name, gender, release_year, rating_value, rating_count,
        brands(name),
        perfume_accords(rank, accords(name))
        """.trimIndent()
    )

    // Requête complète : pour la page individuelle
    private val detailColumns = Columns.raw(
        """
        id, fragrantica_id, name, url, gender, release_year, rating_value, rating_count,
        brands(name, countries(name)),
        perfume_accords(rank, accords(name)),
        perfume_notes(layer, notes(name)),
        perfume_perfumers(perfumers(name))
        """.trimIndent()
    )

    private val filterSeedColumns = Columns.raw(
        """
        brands(name),
        perfume_accords(rank, accords(name)),
        perfume_notes(layer, notes(name))
        """.trimIndent()
    )

    suspend fun getHomePerfumes(count: Long = 10): List<PerfumeSummaryDto> =
        db.from("perfumes").select(listColumns) {
            order("rating_count", Order.DESCENDING)
            limit(count)
        }.decodeList<PerfumeSummaryDto>()

    suspend fun getPerfumeById(id: Int): PerfumeDto? =
        db.from("perfumes").select(detailColumns) {
            filter { eq("id", id) }
        }.decodeSingleOrNull<PerfumeDto>()

    suspend fun topPerfumes(count: Long = 5): List<PerfumeDto> =
        db.from("perfumes").select(detailColumns) {
            order("rating_count", Order.DESCENDING)
            limit(count)
        }.decodeList<PerfumeDto>()

    /**
     * Charge dynamiquement depuis Supabase les options de filtres populaires (marques, accords, notes).
     */
    suspend fun getCatalogueFilterOptions(): CatalogueFilterOptions {
        cachedFilterOptions?.let { return it }

        val topBrandSlugs = listOf(
            "diptyque",
            "hermes",
            "le-labo",
            "guerlain",
            "dior",
            "chanel",
            "yves-saint-laurent",
            "tom-ford",
            "dolce-gabbana",
            "lancome",
            "giorgio-armani",
            "givenchy",
            "acqua-di-parma",
            "mugler",
            "calvin-klein",
            "jean-paul-gaultier",
        )

        val fetchedBrands = db.from("brands").select(Columns.raw("id, name")) {
            filter { isIn("name", topBrandSlugs) }
        }.decodeList<SearchNameRpcDto>().map { it.name }

        val brands = topBrandSlugs.filter { slug ->
            fetchedBrands.any { it.equals(slug, ignoreCase = true) }
        }.ifEmpty { fetchedBrands }

        val topAccordOrder = listOf(
            "woody",
            "citrus",
            "green",
            "aquatic",
            "floral",
            "warm spicy",
            "powdery",
            "aromatic",
            "white floral",
            "fresh spicy",
            "amber",
            "vanilla",
            "fruity",
            "sweet",
            "musky",
            "gourmand",
        )
        val allAccords = getAllAccords().map { it.name }
        val accords = (
            topAccordOrder.filter { pref -> allAccords.any { it.equals(pref, ignoreCase = true) } } +
                allAccords
            ).distinctBy { it.lowercase() }

        val topNoteNames = listOf(
            "bergamot",
            "sandalwood",
            "vanilla",
            "jasmine",
            "musk",
            "patchouli",
            "rose",
            "amber",
            "cedar",
            "mandarin orange",
            "vetiver",
            "fig leaf",
            "fig",
            "sea salt",
            "orange blossom",
            "tonka bean",
            "iris",
            "lavender",
            "lemon",
        )
        val fetchedNotes = db.from("notes").select(Columns.raw("id, name")) {
            filter { isIn("name", topNoteNames) }
        }.decodeList<SearchNameRpcDto>().map { it.name }

        val notes = topNoteNames.filter { target ->
            fetchedNotes.any { it.equals(target, ignoreCase = true) }
        }.ifEmpty { fetchedNotes }

        val options = CatalogueFilterOptions(
            brands = brands,
            accords = accords,
            notes = notes,
        )
        cachedFilterOptions = options
        return options
    }

    /**
     * Charge l'annuaire des marques depuis Supabase (pour l'écran Marques).
     */
    suspend fun getBrandsDirectory(query: String = "", limit: Long = 60): List<BrandWithCountryDto> {
        val q = query.trim()
        val qSlug = normalizeSearchSlug(q)
        return db.from("brands").select(Columns.raw("id, name, countries(name)")) {
            if (q.isNotEmpty()) {
                filter {
                    or {
                        ilike("name", "%$q%")
                        if (qSlug.isNotBlank() && qSlug != q.lowercase()) {
                            ilike("name", "%$qSlug%")
                        }
                    }
                }
            }
            order("name", Order.ASCENDING)
            limit(limit)
        }.decodeList<BrandWithCountryDto>()
    }

    /**
     * Recherche et filtrage dynamique du catalogue de parfums directement sur Supabase.
     */
    suspend fun searchPerfumesRpc(params: PerfumeSearchParams): List<SearchPerfumeRpcDto> {
        val selectedBrandIds = resolveBrandIds(params.brands)
        if (params.brands.isNotEmpty() && selectedBrandIds.isEmpty()) {
            return emptyList()
        }

        val selectedAccordIds = resolveAccordIds(params.accords)
        if (selectedAccordIds == null) {
            return emptyList()
        }

        val selectedNoteIds = resolveNoteIds(params.notes)
        if (selectedNoteIds == null) {
            return emptyList()
        }

        val q = params.query.trim()
        if (q.isEmpty()) {
            return executePerfumesPostgrestQuery(
                params = params,
                brandFilterIds = selectedBrandIds,
                accordFilterIds = selectedAccordIds,
                noteFilterIds = selectedNoteIds,
            )
        }

        val qLower = q.lowercase()
        val qSlug = normalizeSearchSlug(q)
        val qTranslated = translateOlfactiveTerm(q)
        val isTranslatedOlfactive = qTranslated != qLower && qTranslated != qSlug

        val matchingBrands = findBrandsMatchingQuery(q, qSlug)
        val matchingAccordId = findAccordMatchingQuery(qLower, qTranslated)
        val matchingNoteId = findExactNoteMatchingQuery(qLower, qTranslated)

        // Si l'utilisateur a cherché un terme olfactif précis (ex: "fig leaf", "Feuille de figuier", "Sel marin", "Boisé", "woody")
        // qui ne correspond à aucune marque :
        if (matchingBrands.isEmpty() && isTranslatedOlfactive) {
            if (matchingNoteId != null) {
                return executePerfumesPostgrestQuery(
                    params = params,
                    brandFilterIds = selectedBrandIds,
                    accordFilterIds = selectedAccordIds,
                    noteFilterIds = (selectedNoteIds + matchingNoteId).distinct(),
                )
            }
            if (matchingAccordId != null) {
                return executePerfumesPostgrestQuery(
                    params = params,
                    brandFilterIds = selectedBrandIds,
                    accordFilterIds = (selectedAccordIds + matchingAccordId).distinct(),
                    noteFilterIds = selectedNoteIds,
                )
            }
        }

        // 1. Recherche principale sur le nom du parfum ou la marque
        val nameOrBrandResults = executePerfumesPostgrestQuery(
            params = params,
            brandFilterIds = selectedBrandIds,
            accordFilterIds = selectedAccordIds,
            noteFilterIds = selectedNoteIds,
            textQueryRaw = q,
            textQuerySlug = qSlug,
            textQueryTranslated = qTranslated,
            textQueryBrandIds = matchingBrands,
        )
        if (nameOrBrandResults.isNotEmpty()) {
            return nameOrBrandResults
        }

        // 2. Si aucun nom de parfum ni marque ne correspond, on cherche dans les notes, accords ou parfumeurs
        val fallbackNoteId = matchingNoteId ?: findPartialNoteMatchingQuery(qLower, qTranslated)
        if (fallbackNoteId != null) {
            val byNote = executePerfumesPostgrestQuery(
                params = params,
                brandFilterIds = selectedBrandIds,
                accordFilterIds = selectedAccordIds,
                noteFilterIds = (selectedNoteIds + fallbackNoteId).distinct(),
            )
            if (byNote.isNotEmpty()) return byNote
        }

        if (matchingAccordId != null) {
            val byAccord = executePerfumesPostgrestQuery(
                params = params,
                brandFilterIds = selectedBrandIds,
                accordFilterIds = (selectedAccordIds + matchingAccordId).distinct(),
                noteFilterIds = selectedNoteIds,
            )
            if (byAccord.isNotEmpty()) return byAccord
        }

        val matchingPerfumerId = findPerfumerMatchingQuery(q, qSlug)
        if (matchingPerfumerId != null) {
            return executePerfumesPostgrestQuery(
                params = params,
                brandFilterIds = selectedBrandIds,
                accordFilterIds = selectedAccordIds,
                noteFilterIds = selectedNoteIds,
                perfumerFilterId = matchingPerfumerId,
            )
        }

        return emptyList()
    }

    /**
     * Appel de la fonction stockée PostgreSQL `search_names` (autocomplétion marque / note / accord / parfumeur)
     * avec normalisation des accents / espaces et traduction FR -> EN des termes olfactifs.
     */
    suspend fun searchNamesRpc(kind: String, query: String, limit: Int = 8): List<String> {
        val cleaned = query.trim()
        if (cleaned.isEmpty()) {
            val defaults = getCatalogueFilterOptions()
            return when (kind) {
                "brand" -> defaults.brands.take(limit)
                "accord" -> defaults.accords.take(limit)
                "note" -> defaults.notes.take(limit)
                else -> emptyList()
            }
        }

        val slug = normalizeSearchSlug(cleaned)
        val translated = translateOlfactiveTerm(cleaned)

        val candidates = linkedSetOf(cleaned)
        if (slug.isNotBlank() && !slug.equals(cleaned, ignoreCase = true)) {
            candidates.add(slug)
        }
        if (translated.isNotBlank() && !translated.equals(cleaned, ignoreCase = true)) {
            candidates.add(translated)
        }

        val results = mutableListOf<String>()
        for (q in candidates) {
            val rpcMatches = db.rpc(
                function = "search_names",
                parameters = buildJsonObject {
                    put("kind", kind)
                    put("q", q)
                    put("lim", limit)
                }
            ).decodeList<SearchNameRpcDto>().map { it.name }
            results.addAll(rpcMatches)
        }

        return results.distinctBy { it.lowercase() }.take(limit)
    }

    private suspend fun executePerfumesPostgrestQuery(
        params: PerfumeSearchParams,
        brandFilterIds: List<Int>,
        accordFilterIds: List<Int>,
        noteFilterIds: List<Int>,
        perfumerFilterId: Int? = null,
        textQueryRaw: String? = null,
        textQuerySlug: String? = null,
        textQueryTranslated: String? = null,
        textQueryBrandIds: List<Int> = emptyList(),
    ): List<SearchPerfumeRpcDto> {
        val selectParts = mutableListOf(
            "id",
            "name",
            "gender",
            "release_year",
            "rating_value",
            "rating_count",
            "brands!inner(name)",
            "perfume_accords(rank, accords(name))",
        )
        accordFilterIds.forEachIndexed { idx, _ ->
            selectParts.add("fa$idx:perfume_accords!inner()")
        }
        noteFilterIds.forEachIndexed { idx, _ ->
            selectParts.add("fn$idx:perfume_notes!inner()")
        }
        if (perfumerFilterId != null) {
            selectParts.add("fp0:perfume_perfumers!inner()")
        }

        val columns = Columns.raw(selectParts.joinToString(", "))
        val safeLimit = params.limit.coerceAtLeast(1)
        val safeOffset = params.offset.coerceAtLeast(0)

        val response = db.from("perfumes").select(columns = columns) {
            count(Count.EXACT)

            filter {
                if (!params.gender.isNullOrBlank()) {
                    eq("gender", params.gender.lowercase())
                }
                if (brandFilterIds.isNotEmpty()) {
                    isIn("brand_id", brandFilterIds)
                }
                accordFilterIds.forEachIndexed { idx, accordId ->
                    eq("fa$idx.accord_id", accordId)
                }
                noteFilterIds.forEachIndexed { idx, noteId ->
                    eq("fn$idx.note_id", noteId)
                }
                if (perfumerFilterId != null) {
                    eq("fp0.perfumer_id", perfumerFilterId)
                }
                if (params.yearMin != null) {
                    gte("release_year", params.yearMin)
                }
                if (params.yearMax != null) {
                    lte("release_year", params.yearMax)
                }
                if (params.minRating != null) {
                    gte("rating_value", params.minRating)
                }
                if (!textQueryRaw.isNullOrBlank()) {
                    or {
                        ilike("name", "%$textQueryRaw%")
                        if (!textQuerySlug.isNullOrBlank() && !textQuerySlug.equals(textQueryRaw, ignoreCase = true)) {
                            ilike("name", "%$textQuerySlug%")
                        }
                        if (!textQueryTranslated.isNullOrBlank() &&
                            !textQueryTranslated.equals(textQueryRaw, ignoreCase = true) &&
                            !textQueryTranslated.equals(textQuerySlug, ignoreCase = true)
                        ) {
                            ilike("name", "%$textQueryTranslated%")
                        }
                        if (textQueryBrandIds.isNotEmpty()) {
                            isIn("brand_id", textQueryBrandIds)
                        }
                    }
                }
            }

            when (params.sort) {
                "rating" -> {
                    order("rating_value", Order.DESCENDING, nullsFirst = false)
                    order("rating_count", Order.DESCENDING, nullsFirst = false)
                }
                "recent" -> {
                    order("release_year", Order.DESCENDING, nullsFirst = false)
                    order("rating_count", Order.DESCENDING, nullsFirst = false)
                }
                "name" -> {
                    order("name", Order.ASCENDING, nullsFirst = false)
                }
                else -> {
                    order("rating_count", Order.DESCENDING, nullsFirst = false)
                }
            }
            order("id", Order.ASCENDING)

            range(safeOffset.toLong(), (safeOffset + safeLimit - 1).toLong())
        }

        val summaries = response.decodeList<PerfumeSummaryDto>()
        val total = response.countOrNull() ?: summaries.size.toLong()

        return summaries.map { dto ->
            SearchPerfumeRpcDto(
                id = dto.id,
                name = dto.name,
                brandName = dto.brands.name,
                gender = dto.gender,
                releaseYear = dto.releaseYear,
                ratingValue = dto.ratingValue,
                ratingCount = dto.ratingCount,
                accords = dto.accords
                    .sortedBy { it.rank }
                    .map { it.accords.name }
                    .take(3),
                totalCount = total,
            )
        }
    }

    private suspend fun getAllAccords(): List<SearchNameRpcDto> {
        cachedAccords?.let { return it }
        return accordsMutex.withLock {
            cachedAccords?.let { return@withLock it }
            val fetched = db.from("accords")
                .select(Columns.raw("id, name"))
                .decodeList<SearchNameRpcDto>()
            cachedAccords = fetched
            fetched
        }
    }

    private suspend fun resolveBrandIds(brands: List<String>): List<Int> {
        if (brands.isEmpty()) return emptyList()
        val candidateNames = brands.flatMap { raw ->
            val cleaned = raw.trim().lowercase()
            val slug = normalizeSearchSlug(raw)
            listOf(cleaned, slug).filter { it.isNotBlank() }
        }.distinct()

        if (candidateNames.isEmpty()) return emptyList()

        val found = db.from("brands").select(Columns.raw("id, name")) {
            filter {
                or {
                    isIn("name", candidateNames)
                    candidateNames.forEach { candidate ->
                        ilike("name", candidate)
                    }
                }
            }
        }.decodeList<SearchNameRpcDto>()

        return found.map { it.id }.distinct()
    }

    /**
     * Retourne la liste des IDs d'accords sélectionnés, ou null si un accord sélectionné n'existe pas.
     */
    private suspend fun resolveAccordIds(accords: List<String>): List<Int>? {
        if (accords.isEmpty()) return emptyList()
        val allAccords = getAllAccords()
        val ids = mutableListOf<Int>()
        for (raw in accords) {
            val cleaned = raw.trim().lowercase()
            val translated = translateOlfactiveTerm(raw)
            val match = allAccords.firstOrNull { a ->
                a.name.equals(cleaned, ignoreCase = true) ||
                    a.name.equals(translated, ignoreCase = true) ||
                    (translated == "spicy" && a.name.equals("warm spicy", ignoreCase = true))
            } ?: return null
            ids.add(match.id)
        }
        return ids.distinct()
    }

    /**
     * Retourne la liste des IDs de notes sélectionnées, ou null si une note sélectionnée n'existe pas.
     */
    private suspend fun resolveNoteIds(notes: List<String>): List<Int>? {
        if (notes.isEmpty()) return emptyList()
        val ids = mutableListOf<Int>()
        for (raw in notes) {
            val cleaned = raw.trim().lowercase()
            val translated = translateOlfactiveTerm(raw)
            val found = db.from("notes").select(Columns.raw("id, name")) {
                filter {
                    or {
                        ilike("name", cleaned)
                        if (translated != cleaned) {
                            ilike("name", translated)
                        }
                    }
                }
                limit(1)
            }.decodeList<SearchNameRpcDto>()
            val match = found.firstOrNull() ?: return null
            ids.add(match.id)
        }
        return ids.distinct()
    }

    private suspend fun findBrandsMatchingQuery(q: String, qSlug: String): List<Int> {
        return db.from("brands").select(Columns.raw("id, name")) {
            filter {
                or {
                    ilike("name", "%$q%")
                    if (qSlug.isNotBlank() && !qSlug.equals(q, ignoreCase = true)) {
                        ilike("name", "%$qSlug%")
                    }
                }
            }
            limit(25)
        }.decodeList<SearchNameRpcDto>().map { it.id }
    }

    private suspend fun findAccordMatchingQuery(qLower: String, qTranslated: String): Int? {
        val allAccords = getAllAccords()
        return allAccords.firstOrNull { a ->
            a.name.equals(qLower, ignoreCase = true) ||
                a.name.equals(qTranslated, ignoreCase = true) ||
                (qTranslated == "spicy" && a.name.equals("warm spicy", ignoreCase = true))
        }?.id
    }

    private suspend fun findExactNoteMatchingQuery(qLower: String, qTranslated: String): Int? {
        return db.from("notes").select(Columns.raw("id, name")) {
            filter {
                or {
                    ilike("name", qLower)
                    if (qTranslated != qLower) {
                        ilike("name", qTranslated)
                    }
                }
            }
            limit(1)
        }.decodeList<SearchNameRpcDto>().firstOrNull()?.id
    }

    private suspend fun findPartialNoteMatchingQuery(qLower: String, qTranslated: String): Int? {
        return db.from("notes").select(Columns.raw("id, name")) {
            filter {
                or {
                    ilike("name", "%$qLower%")
                    if (qTranslated != qLower) {
                        ilike("name", "%$qTranslated%")
                    }
                }
            }
            limit(1)
        }.decodeList<SearchNameRpcDto>().firstOrNull()?.id
    }

    private suspend fun findPerfumerMatchingQuery(q: String, qSlug: String): Int? {
        return db.from("perfumers").select(Columns.raw("id, name")) {
            filter {
                or {
                    ilike("name", "%$q%")
                    if (qSlug.isNotBlank() && !qSlug.equals(q, ignoreCase = true)) {
                        ilike("name", "%$qSlug%")
                    }
                }
            }
            limit(1)
        }.decodeList<SearchNameRpcDto>().firstOrNull()?.id
    }

    companion object {
        private val accordsMutex = Mutex()
        @Volatile
        private var cachedAccords: List<SearchNameRpcDto>? = null

        @Volatile
        private var cachedFilterOptions: CatalogueFilterOptions? = null
    }
}
