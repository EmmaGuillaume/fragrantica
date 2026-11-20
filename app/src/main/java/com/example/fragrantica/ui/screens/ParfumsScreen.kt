package com.example.fragrantica.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun ParfumsScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier,
) {
    RechercheScreen(
        navController = navController,
        modifier = modifier,
    )
}