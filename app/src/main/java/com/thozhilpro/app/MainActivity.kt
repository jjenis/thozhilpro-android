package com.thozhilpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.thozhilpro.app.data.local.PreferencesManager
import com.thozhilpro.app.ui.navigation.NavGraph
import com.thozhilpro.app.ui.navigation.Routes
import com.thozhilpro.app.ui.screens.ProfileCompletionDialog
import com.thozhilpro.app.ui.theme.ThozhilProTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isLoggedIn = runBlocking { preferencesManager.isLoggedIn() }

        setContent {
            ThozhilProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDest = if (isLoggedIn) Routes.DASHBOARD else Routes.LOGIN

                    val currentUser by preferencesManager.currentUser.collectAsState()
                    val currentRoute = navController.currentBackStackEntryFlow.collectAsState(initial = null)
                    val route = currentRoute.value?.destination?.route

                    val showProfileDialog = currentUser != null &&
                            currentUser?.profileCompleted == false &&
                            route != null &&
                            route != Routes.LOGIN &&
                            route != Routes.REGISTER

                    if (showProfileDialog) {
                        ProfileCompletionDialog(
                            onDismiss = { /* cannot dismiss */ },
                            onComplete = { /* handled inside dialog */ }
                        )
                    }

                    NavGraph(
                        navController = navController,
                        startDestination = startDest
                    )
                }
            }
        }
    }
}
