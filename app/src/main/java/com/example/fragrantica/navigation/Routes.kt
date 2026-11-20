package com.example.fragrantica.navigation

object Routes {
    const val ACCUEIL = "accueil"
    const val RECHERCHE = "recherche"
    const val PARFUMS = "parfums"
    const val MARQUES = "marques"
    const val ACTUALITES = "actualites"

    const val A_PROPOS = "apropos"
    const val AJOUTER = "ajouter"
    const val DETAIL = "detail"
    const val DETAIL_PATTERN = "detail/{perfumeId}"

    fun detail(perfumeId: Int) = "detail/$perfumeId"
}

val menuItems = listOf(
    "Parfums" to Routes.PARFUMS,
    "Marques" to Routes.MARQUES,
    "Actualités" to Routes.ACTUALITES,
    "Ajouter" to Routes.AJOUTER,
    "À propos" to Routes.A_PROPOS,
)