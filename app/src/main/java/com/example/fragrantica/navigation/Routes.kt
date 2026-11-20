package com.example.fragrantica.navigation

object Routes {
    const val ACCUEIL = "accueil"
    const val RECHERCHE = "recherche"
    const val PARFUMS = "parfums"
    const val MARQUES = "marques"
    const val ACTUALITES = "actualites"
    const val A_PROPOS = "apropos"

    const val DETAIL = "detail"
    const val DETAIL_PATTERN = "$DETAIL/{perfumeId}"

    fun detailRoute(id: Int) = "$DETAIL/$id"
}

val menuItems = listOf(
    "Parfums" to Routes.PARFUMS,
    "Marques" to Routes.MARQUES,
    "À propos" to Routes.A_PROPOS,
)