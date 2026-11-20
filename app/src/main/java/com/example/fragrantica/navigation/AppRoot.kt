package com.example.fragrantica.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.fragrantica.ui.components.Header
import com.example.fragrantica.ui.screens.AuthScreen
import com.example.fragrantica.model.AuthViewModel
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun AppRoot() {
    val authVm: AuthViewModel = viewModel()
    val status by authVm.sessionStatus.collectAsState()

    when (status) {
        is SessionStatus.Initializing ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is SessionStatus.Authenticated -> {
            val navController = rememberNavController()
            Scaffold(
                topBar = { Header(navController, onSignOut = authVm::signOut) }
            ) { padding ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        }
        else -> AuthScreen(authVm)   // NotAuthenticated, RefreshFailure
    }
}