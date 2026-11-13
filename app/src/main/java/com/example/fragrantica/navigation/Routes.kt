package com.example.fragrantica.navigation

object Routes {
    const val ACCUEIL = "accueil"
    const val RECHERCHE = "recherche"
    const val PARFUMS = "parfums"
    const val MARQUES = "marques"
    const val ACTUALITES = "actualites"

    const val A_PROPOS = "apropos"
    const val CONNEXION = "connexion"
    const val CREER_COMPTE = "creercompte"
    const val DETAIL = "detail"
}

val menuItems = listOf(
    "Parfums" to Routes.PARFUMS,
    "Marques" to Routes.MARQUES,
    "Actualités" to Routes.ACTUALITES,
    "À propos" to Routes.A_PROPOS,
    "Connexion" to Routes.CONNEXION,
    "Créer mon compte" to Routes.CREER_COMPTE,
)