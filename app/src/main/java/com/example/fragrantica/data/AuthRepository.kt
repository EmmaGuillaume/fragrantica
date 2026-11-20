package com.example.fragrantica.data

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {
    private val auth = SupabaseProvider.client.auth

    val sessionStatus = auth.sessionStatus

    suspend fun signUp(email: String, password: String) =
        auth.signUpWith(Email) { this.email = email; this.password = password }

    suspend fun signIn(email: String, password: String) =
        auth.signInWith(Email) { this.email = email; this.password = password }

    suspend fun signOut() = auth.signOut()
}