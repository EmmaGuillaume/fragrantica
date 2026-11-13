package com.example.fragrantica.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.fragrantica.navigation.Routes.ACCUEIL
import com.example.fragrantica.navigation.Routes.RECHERCHE
import com.example.fragrantica.navigation.menuItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(navController: NavHostController) {
    var menuOuvert by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                "Fragrantica",
                modifier = Modifier.clickable {
                    navController.navigate(ACCUEIL) {
                        popUpTo(ACCUEIL) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(RECHERCHE) { launchSingleTop = true }
            }) {
                Icon(Icons.Default.Search, contentDescription = "Rechercher")
            }

            Box {
                IconButton(onClick = { menuOuvert = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = menuOuvert,
                    onDismissRequest = { menuOuvert = false }
                ) {
                    menuItems.forEach { (titre, route) ->
                        DropdownMenuItem(
                            text = { Text(titre) },
                            onClick = {
                                menuOuvert = false
                                navController.navigate(route) { launchSingleTop = true }
                            }
                        )
                    }
                }
            }
        }
    )
}