package com.example.fragrantica.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fragrantica.ui.screens.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onSignOut: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ACCUEIL,
        modifier = modifier
    ) {
        composable(Routes.ACCUEIL) { AccueilScreen(navController) }
        composable(Routes.RECHERCHE) { RechercheScreen() }
        composable(Routes.PARFUMS) { ParfumsScreen() }
        composable(Routes.MARQUES) { MarquesScreen() }
        composable(Routes.ACTUALITES) { ActualitesScreen() }
        composable(Routes.DETAIL) { AProposScreen(navController) }
        composable(Routes.A_PROPOS) { AProposScreen(navController) }
    }
}