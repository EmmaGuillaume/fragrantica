package com.fragrantica.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val AppColorScheme = lightColorScheme(
    primary = Primaire,
    onPrimary = Blanc,
    primaryContainer = Ciel,
    onPrimaryContainer = Profond,

    secondary = Profond,
    onSecondary = Blanc,
    secondaryContainer = Sable,
    onSecondaryContainer = Texte,

    tertiary = Vert,
    onTertiary = Blanc,

    background = Chaux,
    onBackground = Texte,
    surface = Blanc,
    onSurface = Texte,
    surfaceVariant = Sable,
    onSurfaceVariant = Secondaire,
    outline = Bois,

    error = Erreur,
    onError = Blanc,
    errorContainer = Rose,
    onErrorContainer = Erreur,
)

@Composable
fun FragranticaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content,
    )
}