package com.example.fragrantica.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

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
}