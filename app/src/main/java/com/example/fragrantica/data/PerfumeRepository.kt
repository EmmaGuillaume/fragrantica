package com.example.fragrantica.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order

class PerfumeRepository {
    private val db = SupabaseProvider.client.postgrest

    suspend fun topPerfumes(count: Long = 5): List<PerfumeDto> =
        db.from("perfumes").select(
            Columns.raw(
                """
                id, fragrantica_id, name, url, gender, release_year, rating_value, rating_count,
                brands(name, countries(name)),
                perfume_accords(rank, accords(name)),
                perfume_notes(layer, notes(name)),
                perfume_perfumers(perfumers(name))
                """.trimIndent()
            )
        ) {
            order("rating_count", Order.DESCENDING)
            limit(count)
        }.decodeList<PerfumeDto>()
}