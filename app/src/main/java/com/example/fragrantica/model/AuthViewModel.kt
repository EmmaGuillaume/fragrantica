package com.example.fragrantica.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragrantica.data.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.status.SessionStatus

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    val sessionStatus: StateFlow<SessionStatus> = repo.sessionStatus

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    fun signIn(email: String, password: String) =
        launchAuth { repo.signIn(email.trim(), password) }

    fun signUp(email: String, password: String) =
        launchAuth(info = "Compte créé. Si la confirmation par mail est activée, vérifie ta boîte mail.") {
            repo.signUp(email.trim(), password)
        }

    fun signOut() = launchAuth { repo.signOut() }

    private fun launchAuth(info: String? = null, block: suspend () -> Unit) {
        viewModelScope.launch {
            _ui.value = AuthUiState(loading = true)
            _ui.value = try {
                block()
                AuthUiState(info = info)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AuthUiState(error = e.toFrenchMessage())
            }
        }
    }

    private fun Exception.toFrenchMessage(): String {
        val m = message.orEmpty()
        return when {
            "Invalid login credentials" in m -> "Email ou mot de passe incorrect."
            "already registered" in m -> "Un compte existe déjà avec cet email."
            "Password should be" in m -> "Mot de passe trop court (6 caractères minimum)."
            "valid email" in m || "invalid format" in m -> "Adresse email invalide."
            "Unable to resolve host" in m -> "Pas de connexion internet."
            else -> "Une erreur est survenue : $m"
        }
    }
}