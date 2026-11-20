package com.example.fragrantica.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragrantica.data.SupabaseProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.Calendar

@Serializable
private data class NameItem(val id: Int, val name: String)

class PerfumeRepository(private val client: SupabaseClient) {

    /** kind : "brand" | "note" | "accord" | "perfumer" */
    suspend fun searchNames(kind: String, q: String): List<String> =
        client.postgrest.rpc(
            "search_names",
            buildJsonObject {
                put("kind", kind)
                put("q", q)
                put("lim", 10)
            }
        ).decodeList<NameItem>().map { it.name }

    /** Renvoie l'id du parfum s'il existe déjà, sinon null. */
    suspend fun findPerfume(brand: String, name: String): Int? =
        client.postgrest.rpc(
            "find_perfume",
            buildJsonObject {
                put("p_brand", brand)
                put("p_name", name)
            }
        ).decodeAs<Int?>()

    suspend fun addPerfume(form: FormState): Int =
        client.postgrest.rpc(
            "add_perfume",
            buildJsonObject {
                put("p", buildJsonObject {
                    put("name", form.name.trim())
                    put("brand", form.brand.trim())
                    form.gender?.let { put("gender", it) }
                    form.releaseYear.toIntOrNull()?.let { put("release_year", it) }
                    put("perfumers", form.perfumers.toJsonArray())
                    put("notes", buildJsonObject {
                        put("top", form.top.toJsonArray())
                        put("middle", form.middle.toJsonArray())
                        put("base", form.base.toJsonArray())
                    })
                    put("accords", form.accords.toJsonArray()) // l'ordre = le rank
                })
            }
        ).decodeAs<Int>()

    private fun List<String>.toJsonArray() = JsonArray(map { JsonPrimitive(it) })
}

data class FormState(
    val name: String = "",
    val brand: String = "",
    val gender: String? = null, // "women" | "men" | "unisex"
    val releaseYear: String = "",
    val perfumers: List<String> = emptyList(),
    val top: List<String> = emptyList(),
    val middle: List<String> = emptyList(),
    val base: List<String> = emptyList(),
    val accords: List<String> = emptyList(),
    val existingId: Int? = null, // parfum déjà présent en base
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedId: Int? = null,
) {
    private val currentYear get() = Calendar.getInstance().get(Calendar.YEAR)

    val yearValid: Boolean
        get() = releaseYear.isBlank() || releaseYear.toIntOrNull()?.let { it in 1700..currentYear } == true

    val canSave: Boolean
        get() = name.isNotBlank() && brand.isNotBlank() && yearValid &&
                existingId == null && !isSaving
}

/* ---------- ViewModel ---------- */

class AjoutParfumViewModel(
    private val repo: PerfumeRepository = PerfumeRepository(SupabaseProvider.client),
) : ViewModel() {

    private val _state = MutableStateFlow(FormState())
    val state: StateFlow<FormState> = _state.asStateFlow()

    private var duplicateJob: Job? = null

    fun onName(v: String) { _state.update { it.copy(name = v) }; checkDuplicate() }
    fun onBrand(v: String) { _state.update { it.copy(brand = v) }; checkDuplicate() }
    fun onGender(v: String?) = _state.update { it.copy(gender = v) }
    fun onYear(v: String) = _state.update { it.copy(releaseYear = v.filter(Char::isDigit).take(4)) }
    fun onPerfumers(v: List<String>) = _state.update { it.copy(perfumers = v) }
    fun onTop(v: List<String>) = _state.update { it.copy(top = v) }
    fun onMiddle(v: List<String>) = _state.update { it.copy(middle = v) }
    fun onBase(v: List<String>) = _state.update { it.copy(base = v) }
    fun onAccords(v: List<String>) = _state.update { it.copy(accords = v) }

    suspend fun search(kind: String, q: String): List<String> = repo.searchNames(kind, q)

    /** Vérifie en temps réel (avec debounce) si marque + nom existent déjà. */
    private fun checkDuplicate() {
        duplicateJob?.cancel()
        _state.update { it.copy(existingId = null) }
        val s = _state.value
        if (s.name.isBlank() || s.brand.isBlank()) return
        duplicateJob = viewModelScope.launch {
            delay(500)
            val id = runCatching { repo.findPerfume(s.brand, s.name) }.getOrNull()
            _state.update { it.copy(existingId = id) }
        }
    }

    fun save() {
        val form = _state.value
        if (!form.canSave) return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                val id = repo.addPerfume(form)
                _state.update { it.copy(isSaving = false, savedId = id) }
            } catch (e: PostgrestRestException) {
                val msg = if (e.code == "23505") "Ce parfum existe déjà pour cette marque."
                else "Erreur : ${e.message}"
                _state.update { it.copy(isSaving = false, error = msg) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = "Connexion impossible. Réessaie.") }
            }
        }
    }

    fun consumeSaved() = _state.update { it.copy(savedId = null) }
}