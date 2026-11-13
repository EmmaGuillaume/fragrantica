package com.example.fragrantica.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fragrantica.ui.screens.*

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.ACCUEIL,
        modifier = modifier
    ) {
        composable(Routes.ACCUEIL) { ThemeTestScreen() }
        composable(Routes.RECHERCHE) { RechercheScreen() }
        composable(Routes.PARFUMS) { ParfumsScreen() }
        composable(Routes.MARQUES) { MarquesScreen() }
        composable(Routes.ACTUALITES) { ActualitesScreen() }
        composable(Routes.CONNEXION) { AProposScreen() }
        composable(Routes.CREER_COMPTE) { AProposScreen() }
        composable(Routes.DETAIL) { AProposScreen() }
    }
}