package com.example.fragrantica.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.fragrantica.data.SearchPerfumeRpcDto
import com.example.fragrantica.navigation.Routes
import com.example.fragrantica.ui.components.PerfumeCard
import com.example.fragrantica.ui.components.PerfumeCardVariant
import com.fragrantica.ui.theme.Blanc
import com.fragrantica.ui.theme.Chaux
import com.fragrantica.ui.theme.Ciel
import com.fragrantica.ui.theme.FragranticaTheme
import com.fragrantica.ui.theme.Manrope
import com.fragrantica.ui.theme.Primaire
import com.fragrantica.ui.theme.Profond
import com.fragrantica.ui.theme.Sable
import com.fragrantica.ui.theme.Secondaire
import com.fragrantica.ui.theme.Texte

@Composable
fun RechercheScreen(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier,
    vm: CatalogueViewModel = viewModel(),
) {
    val state by vm.state.collectAsState()

    val navigateTo: (String) -> Unit = { route ->
        navController?.navigate(route) { launchSingleTop = true }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Chaux)
    ) {
        val isDesktop = maxWidth >= 840.dp
        val horizontalPadding = if (isDesktop) 80.dp else 20.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CatalogueTopHeaderSection(
                state = state,
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onQueryChange = vm::onQueryInputChange,
                onSearchSubmit = vm::submitSearch,
                onResetFilters = vm::resetFilters,
                onRemoveBrand = vm::onToggleBrand,
                onRemoveAccord = vm::onToggleAccord,
            )

            CatalogueContentSection(
                state = state,
                vm = vm,
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onPerfumeClick = { id -> navigateTo(Routes.detailRoute(id)) },
            )

            CatalogueFooterSection(
                isDesktop = isDesktop,
                horizontalPadding = horizontalPadding,
                onNavigate = navigateTo,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CatalogueTopHeaderSection(
    state: CatalogueUiState,
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onResetFilters: () -> Unit,
    onRemoveBrand: (String) -> Unit,
    onRemoveAccord: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = if (isDesktop) 36.dp else 16.dp,
                bottom = 24.dp,
            )
    ) {
        Text(
            text = "EXPLORER / PARFUMS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
            ),
            color = Primaire,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isDesktop) {
                "Votre prochain coup de cœur"
            } else {
                "Votre prochain coup de\ncœur"
            },
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = if (isDesktop) 46.sp else 36.sp,
                lineHeight = if (isDesktop) 54.sp else 44.sp,
            ),
            color = Profond,
        )

        Spacer(modifier = Modifier.height(24.dp))

        CatalogueSearchBar(
            query = state.queryInput,
            onQueryChange = onQueryChange,
            placeholder = if (isDesktop) {
                "Rechercher un parfum, une marque, une note..."
            } else {
                "Parfum, marque, note..."
            },
            onSearchClick = onSearchSubmit,
        )

        Spacer(modifier = Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Ciel)
                    .clickable(onClick = onResetFilters)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Tous les parfums",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Profond,
                )
            }

            state.selectedBrands.forEach { brand ->
                ActiveFilterPill(
                    label = "$brand ×",
                    onClick = { onRemoveBrand(brand) },
                )
            }

            state.selectedAccords.forEach { accord ->
                ActiveFilterPill(
                    label = "$accord ×",
                    onClick = { onRemoveAccord(accord) },
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Des découvertes sélectionnées pour vos envies de nature.",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                lineHeight = 23.sp,
            ),
            color = Secondaire,
        )
    }
}

@Composable
private fun ActiveFilterPill(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Blanc)
            .border(1.dp, Sable, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Profond,
        )
    }
}

@Composable
private fun CatalogueSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(50))
            .background(Blanc)
            .border(1.dp, Sable, RoundedCornerShape(50))
            .padding(start = 22.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondaire,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Texte),
                cursorBrush = SolidColor(Primaire),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Button(
            onClick = onSearchClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primaire,
                contentColor = Blanc,
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            modifier = Modifier.height(48.dp),
        ) {
            Text(
                text = "Rechercher",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun CatalogueContentSection(
    state: CatalogueUiState,
    vm: CatalogueViewModel,
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onPerfumeClick: (Int) -> Unit,
) {
    var mobileFiltersOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }

    val countLabel = if (state.totalCount <= 1L) {
        "${state.totalCount} parfum à découvrir"
    } else {
        "${state.totalCount} parfums à découvrir"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Chaux)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 8.dp,
                bottom = 52.dp,
            )
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.Top,
            ) {
                AffinerLaRechercheCard(
                    state = state,
                    vm = vm,
                    modifier = Modifier.width(248.dp),
                )

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = countLabel,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = Profond,
                        )
                        Box {
                            Text(
                                text = "Trier par : ${state.sortOption.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Secondaire,
                                modifier = Modifier.clickable { sortMenuOpen = true },
                            )
                            SortDropdownMenu(
                                expanded = sortMenuOpen,
                                selected = state.sortOption,
                                onDismiss = { sortMenuOpen = false },
                                onSelect = {
                                    sortMenuOpen = false
                                    vm.onSelectSort(it)
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PerfumeResultsGrid(
                        perfumes = state.perfumes,
                        isLoading = state.isLoading,
                        columns = 3,
                        horizontalSpacing = 24.dp,
                        verticalSpacing = 32.dp,
                        onPerfumeClick = onPerfumeClick,
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    CataloguePaginationBar(
                        currentPage = state.currentPage,
                        totalPages = state.totalPages,
                        onSelectPage = vm::onSelectPage,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Notes et nombres d’avis donnés à titre d’exemple.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondaire,
                    )
                }
            }
        } else {
            Text(
                text = countLabel,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Profond,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (mobileFiltersOpen) Ciel else Blanc)
                        .border(1.dp, Primaire, RoundedCornerShape(50))
                        .clickable { mobileFiltersOpen = !mobileFiltersOpen }
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Filtres",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = Profond,
                    )
                }

                Box {
                    Box(
                        modifier = Modifier
                            .height(42.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Blanc)
                            .border(1.dp, Sable, RoundedCornerShape(50))
                            .clickable { sortMenuOpen = true }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = state.sortOption.label,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = Profond,
                        )
                    }

                    SortDropdownMenu(
                        expanded = sortMenuOpen,
                        selected = state.sortOption,
                        onDismiss = { sortMenuOpen = false },
                        onSelect = {
                            sortMenuOpen = false
                            vm.onSelectSort(it)
                        },
                    )
                }
            }

            AnimatedVisibility(visible = mobileFiltersOpen) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    AffinerLaRechercheCard(
                        state = state,
                        vm = vm,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PerfumeResultsGrid(
                perfumes = state.perfumes,
                isLoading = state.isLoading,
                columns = 2,
                horizontalSpacing = 16.dp,
                verticalSpacing = 28.dp,
                onPerfumeClick = onPerfumeClick,
            )

            Spacer(modifier = Modifier.height(36.dp))

            CataloguePaginationBar(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                onSelectPage = vm::onSelectPage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Notes et nombres d’avis donnés à titre d’exemple.",
                style = MaterialTheme.typography.bodySmall,
                color = Secondaire,
            )
        }
    }
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    selected: CatalogueSortOption,
    onDismiss: () -> Unit,
    onSelect: (CatalogueSortOption) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(Blanc),
    ) {
        CatalogueSortOption.entries.forEach { option ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (option == selected) FontWeight.SemiBold else FontWeight.Normal,
                        ),
                        color = if (option == selected) Primaire else Texte,
                    )
                },
                onClick = { onSelect(option) },
            )
        }
    }
}

@Composable
private fun AffinerLaRechercheCard(
    state: CatalogueUiState,
    vm: CatalogueViewModel,
    modifier: Modifier = Modifier,
) {
    var showNoteInput by remember { mutableStateOf(false) }
    var showYearEditor by remember { mutableStateOf(false) }
    var minYearInput by remember(state.yearMin) { mutableStateOf(state.yearMin.toString()) }
    var maxYearInput by remember(state.yearMax) { mutableStateOf(state.yearMax.toString()) }

    val genderOptions = listOf(
        null to "Tous",
        "women" to "Femme",
        "men" to "Homme",
        "unisex" to "Mixte",
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Blanc)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Text(
            text = "Affiner la recherche",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )

        // 1. Genre
        FilterGroup(title = "Genre") {
            genderOptions.forEach { (genderKey, label) ->
                FilterCheckboxRow(
                    label = label,
                    checked = state.selectedGender == genderKey,
                    onClick = { vm.onSelectGender(genderKey) },
                )
            }
        }

        // 2. Année de sortie
        FilterGroup(title = "Année de sortie") {
            Text(
                text = "De ${state.yearMin} à ${state.yearMax}",
                style = MaterialTheme.typography.bodySmall,
                color = Secondaire,
                modifier = Modifier.clickable { showYearEditor = !showYearEditor },
            )
            AnimatedVisibility(visible = showYearEditor) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmallFilterTextField(
                        value = minYearInput,
                        onValueChange = { raw ->
                            minYearInput = raw.filter(Char::isDigit).take(4)
                            val minY = minYearInput.toIntOrNull()
                            val maxY = maxYearInput.toIntOrNull() ?: state.yearMax
                            if (minY != null && minY in 1900..2026) {
                                vm.onYearRangeChange(minY, maxY)
                            }
                        },
                        placeholder = "1990",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    Text("–", color = Secondaire)
                    SmallFilterTextField(
                        value = maxYearInput,
                        onValueChange = { raw ->
                            maxYearInput = raw.filter(Char::isDigit).take(4)
                            val minY = minYearInput.toIntOrNull() ?: state.yearMin
                            val maxY = maxYearInput.toIntOrNull()
                            if (maxY != null && maxY in 1900..2026) {
                                vm.onYearRangeChange(minY, maxY)
                            }
                        },
                        placeholder = "2026",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // 3. Marque (avec autocomplétion via fonction stockée search_names)
        FilterGroup(title = "Marque") {
            SmallFilterTextField(
                value = state.brandSearchInput,
                onValueChange = vm::onBrandSearchInputChange,
                placeholder = "Rechercher une marque...",
                onDone = {
                    if (state.brandSearchInput.isNotBlank()) {
                        vm.onToggleBrand(state.brandSearchInput.trim())
                    }
                },
            )
            if (state.brandSuggestions.isNotEmpty()) {
                AutocompleteSuggestionsDropdown(
                    suggestions = state.brandSuggestions,
                    onPick = vm::onToggleBrand,
                )
            }
            val visibleBrands = if (state.brandSearchInput.isBlank()) {
                state.availableBrands.take(4)
            } else {
                state.availableBrands.filter {
                    it.contains(state.brandSearchInput.trim(), ignoreCase = true)
                }
            }
            visibleBrands.forEach { brand ->
                FilterCheckboxRow(
                    label = brand,
                    checked = state.selectedBrands.any { it.equals(brand, ignoreCase = true) },
                    onClick = { vm.onToggleBrand(brand) },
                )
            }
        }

        // 4. Notes (avec autocomplétion via fonction stockée search_names)
        FilterGroup(title = "Notes") {
            Text(
                text = "Ajouter une note +",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = Secondaire,
                modifier = Modifier.clickable { showNoteInput = !showNoteInput },
            )
            AnimatedVisibility(visible = showNoteInput) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallFilterTextField(
                        value = state.noteSearchInput,
                        onValueChange = vm::onNoteSearchInputChange,
                        placeholder = "Ex. Feuille de figuier...",
                        onDone = {
                            if (state.noteSearchInput.isNotBlank()) {
                                vm.onAddNote(state.noteSearchInput)
                                showNoteInput = false
                            }
                        },
                    )
                    if (state.noteSuggestions.isNotEmpty()) {
                        AutocompleteSuggestionsDropdown(
                            suggestions = state.noteSuggestions,
                            onPick = { picked ->
                                vm.onAddNote(picked)
                                showNoteInput = false
                            },
                        )
                    }
                }
            }
            state.selectedNotes.forEach { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = Secondaire,
                    modifier = Modifier.clickable { vm.onRemoveNote(note) },
                )
            }
        }

        // 5. Accords
        FilterGroup(title = "Accords") {
            state.availableAccords.take(3).forEach { accord ->
                FilterCheckboxRow(
                    label = accord,
                    checked = state.selectedAccords.any { it.equals(accord, ignoreCase = true) },
                    onClick = { vm.onToggleAccord(accord) },
                )
            }
        }

        // 6. Note minimale
        FilterGroup(title = "Note minimale") {
            Text(
                text = "★ 4 et plus",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (state.minRating4Plus) FontWeight.Bold else FontWeight.Normal,
                ),
                color = if (state.minRating4Plus) Primaire else Secondaire,
                modifier = Modifier.clickable { vm.onToggleMinRating4Plus() },
            )
        }

        // Bouton Réinitialiser
        Box(
            modifier = Modifier
                .height(40.dp)
                .clip(RoundedCornerShape(50))
                .background(Blanc)
                .border(1.dp, Primaire, RoundedCornerShape(50))
                .clickable(onClick = vm::resetFilters)
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Réinitialiser",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Profond,
            )
        }
    }
}

@Composable
private fun FilterGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Profond,
        )
        content()
    }
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (checked) "☑" else "☐",
            style = MaterialTheme.typography.bodyMedium,
            color = if (checked) Profond else Secondaire,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
            ),
            color = Secondaire,
        )
    }
}

@Composable
private fun SmallFilterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    onDone: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Chaux.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodySmall,
                color = Secondaire,
                maxLines = 1,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = Texte),
            cursorBrush = SolidColor(Primaire),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AutocompleteSuggestionsDropdown(
    suggestions: List<String>,
    onPick: (String) -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(8.dp),
        color = Chaux,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            suggestions.forEach { item ->
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = Profond,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(item) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun PerfumeResultsGrid(
    perfumes: List<SearchPerfumeRpcDto>,
    isLoading: Boolean,
    columns: Int,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    onPerfumeClick: (Int) -> Unit,
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 48.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = Primaire)
        }
        return
    }

    if (perfumes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Blanc)
                .padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Aucun parfum ne correspond à votre recherche.",
                style = MaterialTheme.typography.bodyLarge,
                color = Secondaire,
            )
        }
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
    ) {
        perfumes.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
                verticalAlignment = Alignment.Top,
            ) {
                rowItems.forEach { dto ->
                    PerfumeCard(
                        perfume = dto.toCardPerfume(),
                        variant = PerfumeCardVariant.Vertical,
                        onClick = { onPerfumeClick(dto.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CataloguePaginationBar(
    currentPage: Int,
    totalPages: Int,
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visiblePages = (1..minOf(maxOf(totalPages, 3), 5)).toList()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        visiblePages.forEach { page ->
            val isSelected = page == currentPage
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Ciel else Blanc)
                    .then(
                        if (isSelected) Modifier
                        else Modifier.border(1.dp, Sable, CircleShape)
                    )
                    .clickable { onSelectPage(page) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = page.toString(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Profond,
                )
            }
        }

        Box(
            modifier = Modifier
                .height(40.dp)
                .clip(RoundedCornerShape(50))
                .background(Blanc)
                .border(1.dp, Sable, RoundedCornerShape(50))
                .clickable {
                    val nextPage = if (currentPage < visiblePages.last()) currentPage + 1 else 1
                    onSelectPage(nextPage)
                }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Suivant →",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Profond,
            )
        }
    }
}

@Composable
private fun CatalogueFooterSection(
    isDesktop: Boolean,
    horizontalPadding: Dp,
    onNavigate: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }

    val footerLinks = listOf(
        "Fragrantica" to Routes.ACCUEIL,
        "À propos" to Routes.A_PROPOS,
        "Contact" to Routes.A_PROPOS,
        "Instagram" to Routes.A_PROPOS,
        "Pinterest" to Routes.A_PROPOS,
        "Mentions légales" to Routes.A_PROPOS,
        "Confidentialité" to Routes.A_PROPOS,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Profond)
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                top = 48.dp,
                bottom = 44.dp,
            )
    ) {
        if (isDesktop) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Un peu de parfum dans votre boîte mail.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Blanc,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Nos découvertes et histoires olfactives, une fois par mois.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ciel,
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
                CatalogueNewsletterPill(
                    email = email,
                    onEmailChange = { email = it },
                    modifier = Modifier.width(440.dp),
                )
            }
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                footerLinks.forEachIndexed { index, (label, route) ->
                    Text(
                        text = label,
                        style = if (index == 0) {
                            MaterialTheme.typography.titleMedium.copy(
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                        color = if (index == 0) Blanc else Ciel,
                        modifier = Modifier.clickable { onNavigate(route) },
                    )
                }
            }
        } else {
            Text(
                text = "Un peu de parfum dans votre\nboîte mail.",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                ),
                color = Blanc,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nos découvertes et histoires olfactives, une fois par\nmois.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                ),
                color = Ciel,
            )
            Spacer(modifier = Modifier.height(24.dp))
            CatalogueNewsletterPill(
                email = email,
                onEmailChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                footerLinks.forEachIndexed { index, (label, route) ->
                    Text(
                        text = label,
                        style = if (index == 0) {
                            MaterialTheme.typography.titleMedium.copy(
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                            )
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                        color = if (index == 0) Blanc else Ciel,
                        modifier = Modifier.clickable { onNavigate(route) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "© 2026 Fragrantica · Le parfum est une histoire de partage.",
            style = MaterialTheme.typography.bodySmall,
            color = Ciel.copy(alpha = 0.85f),
        )
    }
}

@Composable
private fun CatalogueNewsletterPill(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(Blanc)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (email.isEmpty()) {
                Text(
                    text = "Votre adresse e-mail",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Secondaire,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = email,
                onValueChange = onEmailChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Texte),
                cursorBrush = SolidColor(Primaire),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Text(
            text = "S’inscrire →",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = Primaire,
            modifier = Modifier.clickable { onEmailChange("") },
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 2400)
@Composable
fun RechercheScreenMobilePreview() {
    FragranticaTheme {
        RechercheScreen()
    }
}

@Preview(showBackground = true, widthDp = 1440, heightDp = 2225)
@Composable
fun RechercheScreenDesktopPreview() {
    FragranticaTheme {
        RechercheScreen()
    }
}
