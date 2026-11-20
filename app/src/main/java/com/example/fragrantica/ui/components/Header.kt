package com.example.fragrantica.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fragrantica.navigation.Routes
import com.example.fragrantica.navigation.Routes.ACCUEIL
import com.example.fragrantica.navigation.Routes.RECHERCHE
import com.example.fragrantica.navigation.menuItems
import com.fragrantica.ui.theme.Blanc
import com.fragrantica.ui.theme.Chaux
import com.fragrantica.ui.theme.Manrope
import com.fragrantica.ui.theme.Primaire
import com.fragrantica.ui.theme.Profond
import com.fragrantica.ui.theme.Sable
import com.fragrantica.ui.theme.Secondaire
import com.fragrantica.ui.theme.Texte

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    navController: NavHostController,
    onSignOut: (() -> Unit)? = null,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
    ) {
        if (maxWidth >= 840.dp) {
            DesktopHeader(navController = navController, onSignOut = onSignOut)
        } else {
            MobileHeader(navController = navController, onSignOut = onSignOut)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MobileHeader(
    navController: NavHostController,
    onSignOut: (() -> Unit)? = null,
) {
    var menuOuvert by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Chaux,
            scrolledContainerColor = Chaux,
            titleContentColor = Profond,
            actionIconContentColor = Profond,
        ),
        title = {
            Text(
                text = "Fragrantica",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Profond,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable {
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
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = Profond,
                )
            }

            Box {
                IconButton(onClick = { menuOuvert = true }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Profond,
                    )
                }
                DropdownMenu(
                    expanded = menuOuvert,
                    onDismissRequest = { menuOuvert = false },
                    modifier = Modifier.background(Blanc)
                ) {
                    menuItems.forEach { (titre, route) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = titre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Texte,
                                )
                            },
                            onClick = {
                                menuOuvert = false
                                navController.navigate(route) { launchSingleTop = true }
                            }
                        )
                    }
                    if (onSignOut != null) {
                        DropdownMenuItem(
                            text = { Text("Déconnexion") },
                            onClick = {
                                menuOuvert = false
                                onSignOut()
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun DesktopHeader(
    navController: NavHostController,
    onSignOut: (() -> Unit)? = null,
) {
    val desktopLinks = listOf(
        "Parfums" to Routes.PARFUMS,
        "Marques" to Routes.MARQUES,
        "Actualités" to Routes.ACTUALITES,
        "À propos" to Routes.A_PROPOS,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(horizontal = 48.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Text(
                text = "Fragrantica",
                fontFamily = Manrope,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Profond,
                modifier = Modifier.clickable {
                    navController.navigate(ACCUEIL) {
                        popUpTo(ACCUEIL) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

            desktopLinks.forEach { (titre, route) ->
                Text(
                    text = titre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Texte,
                    modifier = Modifier.clickable {
                        navController.navigate(route) { launchSingleTop = true }
                    }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                modifier = Modifier
                    .width(340.dp)
                    .height(42.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Blanc)
                    .border(1.dp, Sable, RoundedCornerShape(50))
                    .clickable {
                        navController.navigate(RECHERCHE) { launchSingleTop = true }
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Rechercher",
                    tint = Secondaire,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rechercher un parfum",
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondaire,
                )
            }

            Text(
                text = if (onSignOut != null) "Déconnexion" else "Connexion",
                style = MaterialTheme.typography.labelLarge,
                color = Primaire,
                modifier = Modifier.clickable {
                    onSignOut?.invoke()
                }
            )
        }
    }
}
