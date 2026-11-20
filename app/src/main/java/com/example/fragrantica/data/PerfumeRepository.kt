package com.example.fragrantica.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class PerfumeSearchParams(
    val query: String = "",
    val gender: String? = null,
    val brands: List<String> = emptyList(),
    val accords: List<String> = emptyList(),
    val notes: List<String> = emptyList(),
    val yearMin: Int? = 1990,
    val yearMax: Int? = 2026,
    val minRating: Double? = null,
    val sort: String = "popularity",
    val limit: Int = 6,
    val offset: Int = 0,
)

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
     * Appel de la fonction stockée PostgreSQL `search_perfumes` via RPC Supabase.
     */
    suspend fun searchPerfumesRpc(params: PerfumeSearchParams): List<SearchPerfumeRpcDto> =
        db.rpc(
            function = "search_perfumes",
            parameters = buildJsonObject {
                put("p_query", params.query.trim())
                if (params.gender.isNullOrBlank()) put("p_gender", JsonNull) else put("p_gender", params.gender)
                if (params.brands.isEmpty()) put("p_brands", JsonNull) else put("p_brands", params.brands.toJsonArray())
                if (params.accords.isEmpty()) put("p_accords", JsonNull) else put("p_accords", params.accords.toJsonArray())
                if (params.notes.isEmpty()) put("p_notes", JsonNull) else put("p_notes", params.notes.toJsonArray())
                if (params.yearMin == null) put("p_year_min", JsonNull) else put("p_year_min", params.yearMin)
                if (params.yearMax == null) put("p_year_max", JsonNull) else put("p_year_max", params.yearMax)
                if (params.minRating == null) put("p_min_rating", JsonNull) else put("p_min_rating", params.minRating)
                put("p_sort", params.sort)
                put("p_limit", params.limit)
                put("p_offset", params.offset)
            }
        ).decodeList<SearchPerfumeRpcDto>()

    /**
     * Appel de la fonction stockée PostgreSQL `search_names` (autocomplétion marque / note / accord).
     * kind : "brand" | "note" | "accord" | "perfumer"
     */
    suspend fun searchNamesRpc(kind: String, query: String, limit: Int = 8): List<String> =
        db.rpc(
            function = "search_names",
            parameters = buildJsonObject {
                put("kind", kind)
                put("q", query.trim())
                put("lim", limit)
            }
        ).decodeList<SearchNameRpcDto>().map { it.name }

    private fun List<String>.toJsonArray() = JsonArray(map { JsonPrimitive(it) })
}