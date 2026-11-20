package com.example.fragrantica.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fragrantica.data.BrandWithCountryDto
import com.example.fragrantica.data.PerfumeRepository
import com.example.fragrantica.navigation.Routes
import com.fragrantica.ui.theme.Blanc
import com.fragrantica.ui.theme.Chaux
import com.fragrantica.ui.theme.Primaire
import com.fragrantica.ui.theme.Profond
import com.fragrantica.ui.theme.Sable
import com.fragrantica.ui.theme.Secondaire
import com.fragrantica.ui.theme.Texte
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarquesScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier,
) {
    val repo = remember { PerfumeRepository() }
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var brands by remember { mutableStateOf<List<BrandWithCountryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun loadBrands(q: String) {
        scope.launch {
            isLoading = true
            error = null
            try {
                brands = repo.getBrandsDirectory(query = q, limit = 60)
            } catch (e: Exception) {
                error = "Impossible de charger les marques depuis Supabase."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(query) {
        if (query.isNotBlank()) delay(250)
        loadBrands(query)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Chaux)
    ) {
        val isDesktop = maxWidth >= 840.dp
        val horizontalPadding = if (isDesktop) 80.dp else 20.dp
        val columns = if (isDesktop) 4 else 2

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 28.dp),
        ) {
            Text(
                text = "EXPLORER / MARQUES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                ),
                color = Primaire,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Maisons de parfum",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = if (isDesktop) 42.sp else 32.sp,
                ),
                color = Profond,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Blanc)
                    .border(1.dp, Sable, RoundedCornerShape(50))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Rechercher parmi les 1 060 marques...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Secondaire,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Texte),
                    cursorBrush = SolidColor(Primaire),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            when {
                isLoading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Primaire)
                }

                error != null -> Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { loadBrands(query) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primaire, contentColor = Blanc),
                    ) {
                        Text("Réessayer")
                    }
                }

                brands.isEmpty() -> Text(
                    text = "Aucune marque trouvée pour « $query ».",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Secondaire,
                )

                else -> Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    brands.chunked(columns).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            rowItems.forEach { brand ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Blanc)
                                        .border(1.dp, Sable, RoundedCornerShape(16.dp))
                                        .clickable {
                                            navController?.navigate(Routes.rechercheRoute(brand = brand.name)) {
                                                launchSingleTop = true
                                            }
                                        }
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = formatDisplayName(brand.name),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                        ),
                                        color = Profond,
                                    )
                                    Text(
                                        text = brand.countries?.name ?: "Découvrir les parfums →",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Secondaire,
                                    )
                                }
                            }
                            repeat(columns - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
