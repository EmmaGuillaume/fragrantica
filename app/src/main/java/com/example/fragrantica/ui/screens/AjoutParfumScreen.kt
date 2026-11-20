package com.example.fragrantica.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragrantica.model.AjoutParfumViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjoutParfumScreen(
    onBack: () -> Unit,
    onSaved: (perfumeId: Int) -> Unit,
    onOpenExisting: (perfumeId: Int) -> Unit,
    vm: AjoutParfumViewModel = viewModel(),
) {
    val s by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.savedId) {
        s.savedId?.let { onSaved(it); vm.consumeSaved() }
    }
    LaunchedEffect(s.error) {
        s.error?.let { snackbar.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau parfum") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Annuler") } },
                actions = {
                    TextButton(onClick = vm::save, enabled = s.canSave) {
                        if (s.isSaving) CircularProgressIndicator(Modifier.padding(4.dp), strokeWidth = 2.dp)
                        else Text("Enregistrer")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // ---- Infos générales ----
            Section("Infos générales") {
                OutlinedTextField(
                    value = s.name,
                    onValueChange = vm::onName,
                    label = { Text("Nom *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                AutocompleteField(
                    value = s.brand,
                    onValueChange = vm::onBrand,
                    label = "Marque *",
                    search = { vm.search("brand", it) },
                )

                s.existingId?.let { id ->
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth().clickable { onOpenExisting(id) },
                    ) {
                        Text(
                            "Ce parfum existe déjà. Touche ici pour ouvrir sa fiche.",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }

                GenderSelector(selected = s.gender, onSelect = vm::onGender)

                OutlinedTextField(
                    value = s.releaseYear,
                    onValueChange = vm::onYear,
                    label = { Text("Année de sortie") },
                    singleLine = true,
                    isError = !s.yearValid,
                    supportingText = { if (!s.yearValid) Text("Année invalide") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // ---- Parfumeurs ----
            Section("Parfumeur(s)") {
                TagInput(
                    label = "Ajouter un parfumeur",
                    tags = s.perfumers,
                    onTagsChange = vm::onPerfumers,
                    search = { vm.search("perfumer", it) },
                )
            }

            // ---- Pyramide olfactive ----
            Section("Pyramide olfactive") {
                TagInput("Notes de tête", s.top, vm::onTop, { vm.search("note", it) })
                TagInput("Notes de cœur", s.middle, vm::onMiddle, { vm.search("note", it) })
                TagInput("Notes de fond", s.base, vm::onBase, { vm.search("note", it) })
            }

            // ---- Accords ----
            Section("Accords principaux (max 5)") {
                Text(
                    "L'ordre d'ajout définit l'importance de l'accord.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TagInput(
                    label = "Ajouter un accord",
                    tags = s.accords,
                    onTagsChange = vm::onAccords,
                    search = { vm.search("accord", it) },
                    max = 5,
                    numbered = true,
                )
            }

            Button(
                onClick = vm::save,
                enabled = s.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Enregistrer") }
        }
    }
}

/* Composants */

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderSelector(selected: String?, onSelect: (String?) -> Unit) {
    val options = listOf("women" to "Femme", "men" to "Homme", "unisex" to "Unisexe")
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (value, label) ->
            SegmentedButton(
                selected = selected == value,
                onClick = { onSelect(if (selected == value) null else value) }, // retoucher = désélectionner
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
            ) { Text(label) }
        }
    }
}

/** Liste de suggestions, avec debounce de 300 ms. */
@Composable
private fun rememberSuggestions(
    query: String,
    search: suspend (String) -> List<String>,
): List<String> {
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    LaunchedEffect(query) {
        if (query.isBlank()) {
            suggestions = emptyList()
            return@LaunchedEffect
        }
        delay(300)
        suggestions = runCatching { search(query.trim()) }.getOrDefault(emptyList())
    }
    return suggestions
}

@Composable
private fun SuggestionList(
    items: List<String>,
    createLabel: String?, // « Créer "xxx" » ou null
    onPick: (String) -> Unit,
) {
    if (items.isEmpty() && createLabel == null) return
    Surface(
        tonalElevation = 3.dp,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            items.forEach { item ->
                Text(
                    item,
                    modifier = Modifier.fillMaxWidth().clickable { onPick(item) }.padding(12.dp),
                )
            }
            createLabel?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().clickable { onPick(createLabel.substringAfter('«').substringBeforeLast('»').trim()) }
                        .padding(12.dp),
                )
            }
        }
    }
}

/** Champ à valeur unique avec autocomplétion (marque). */
@Composable
fun AutocompleteField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    search: suspend (String) -> List<String>,
) {
    var showSuggestions by remember { mutableStateOf(false) }
    val suggestions = rememberSuggestions(value, search)
    val exactMatch = suggestions.any { it.equals(value.trim(), ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it); showSuggestions = true },
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (showSuggestions && value.isNotBlank() && !exactMatch) {
            SuggestionList(
                items = suggestions,
                createLabel = "Créer « ${value.trim()} »",
                onPick = { onValueChange(it); showSuggestions = false },
            )
        }
    }
}

/** Champ multi-valeurs avec chips, autocomplétion et création. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagInput(
    label: String,
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    search: suspend (String) -> List<String>,
    max: Int = Int.MAX_VALUE,
    numbered: Boolean = false,
) {
    var text by remember { mutableStateOf("") }
    val suggestions = rememberSuggestions(text, search)
        .filter { s -> tags.none { it.equals(s, ignoreCase = true) } }

    fun addTag(raw: String) {
        val tag = raw.trim()
        if (tag.isEmpty() || tags.size >= max) return
        if (tags.any { it.equals(tag, ignoreCase = true) }) return // dédoublonnage insensible à la casse
        onTagsChange(tags + tag)
        text = ""
    }

    val full = tags.size >= max
    val exactMatch = suggestions.any { it.equals(text.trim(), ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                tags.forEachIndexed { index, tag ->
                    InputChip(
                        selected = false,
                        onClick = { onTagsChange(tags - tag) },
                        label = { Text(if (numbered) "${index + 1}. $tag" else tag) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Retirer $tag")
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(label) },
            singleLine = true,
            enabled = !full,
            supportingText = { if (full) Text("Maximum atteint ($max)") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { addTag(text) }),
            modifier = Modifier.fillMaxWidth(),
        )

        if (!full && text.isNotBlank()) {
            SuggestionList(
                items = suggestions,
                createLabel = if (exactMatch) null else "Créer « ${text.trim()} »",
                onPick = ::addTag,
            )
        }
    }
}