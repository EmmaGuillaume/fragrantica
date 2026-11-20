package com.example.fragrantica.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable(Routes.RECHERCHE) { RechercheScreen(navController) }
        composable(Routes.PARFUMS) { ParfumsScreen(navController) }
        composable(Routes.MARQUES) { MarquesScreen() }
        composable(Routes.ACTUALITES) { ActualitesScreen() }
        composable(Routes.DETAIL) { AProposScreen(navController) }
        composable(Routes.A_PROPOS) { AProposScreen(navController) }

        composable(Routes.AJOUTER) {
            AjoutParfumScreen(
                onBack = { navController.popBackStack() },
                onSaved = { perfumeId ->
                    navController.navigate(Routes.detailRoute(perfumeId)) {
                        popUpTo(Routes.AJOUTER) { inclusive = true }
                    }
                },
                onOpenExisting = { perfumeId ->
                    navController.navigate(Routes.detailRoute(perfumeId))
                },
            )
        }

        composable(
            route = Routes.DETAIL_PATTERN,
            arguments = listOf(navArgument("perfumeId") { type = NavType.IntType })
        ) {
            PerfumeDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}