package com.example.fragrantica.navigation

import android.net.Uri

object Routes {
    const val ACCUEIL = "accueil"
    const val RECHERCHE = "recherche"
    const val PARFUMS = "parfums"
    const val MARQUES = "marques"
    const val ACTUALITES = "actualites"

    const val A_PROPOS = "apropos"
    const val AJOUTER = "ajouter"

    const val DETAIL = "detail"

    const val DETAIL_PATTERN = "$DETAIL/{perfumeId}"
    const val RECHERCHE_PATTERN = "$RECHERCHE?q={q}&brand={brand}&accord={accord}&note={note}"

    fun detailRoute(id: Int) = "$DETAIL/$id"

    fun rechercheRoute(
        q: String? = null,
        brand: String? = null,
        accord: String? = null,
        note: String? = null,
    ): String {
        val params = buildList {
            if (!q.isNullOrBlank()) add("q=${Uri.encode(q.trim())}")
            if (!brand.isNullOrBlank()) add("brand=${Uri.encode(brand.trim())}")
            if (!accord.isNullOrBlank()) add("accord=${Uri.encode(accord.trim())}")
            if (!note.isNullOrBlank()) add("note=${Uri.encode(note.trim())}")
        }
        return if (params.isEmpty()) RECHERCHE else "$RECHERCHE?${params.joinToString("&")}"
    }
}

val menuItems = listOf(
    "Parfums" to Routes.PARFUMS,
    "Marques" to Routes.MARQUES,
    "Actualités" to Routes.ACTUALITES,
    "Ajouter" to Routes.AJOUTER,
    "À propos" to Routes.A_PROPOS,
)
