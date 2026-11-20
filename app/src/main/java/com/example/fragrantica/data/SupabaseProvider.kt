package com.example.fragrantica.data

import com.example.fragrantica.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.MemoryCodeVerifierCache
import io.github.jan.supabase.auth.MemorySessionManager
import io.github.jan.supabase.auth.SettingsCodeVerifierCache
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth) {
            sessionManager = runCatching { SettingsSessionManager() }
                .getOrElse { MemorySessionManager() }
            codeVerifierCache = runCatching { SettingsCodeVerifierCache() }
                .getOrElse { MemoryCodeVerifierCache() }
        }
        install(Postgrest)
    }
}
