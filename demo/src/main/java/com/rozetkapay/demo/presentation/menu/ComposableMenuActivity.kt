package com.rozetkapay.demo.presentation.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.demo.presentation.tokenization.TokenizationSeparateScreen
import kotlinx.serialization.Serializable

class ComposableMenuActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RozetkaPayDemoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { _ ->
                    val navController = rememberNavController()
                    NavHost(
                        modifier = Modifier.padding(),
                        navController = navController,
                        startDestination = Route.Menu,
                    ) {
                        composable<Route.Menu> {
                            MenuScreen(
                                subtitle = "Jetpack Compose",
                                onNavigationEvent = { route ->
                                    navController.navigate(route)
                                }
                            )
                        }
                        composable<Route.TokenizationSeparate> {
                            TokenizationSeparateScreen(
                                onBack = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, ComposableMenuActivity::class.java)
    }
}

sealed class Route {
    @Serializable
    data object Menu : Route()

    @Serializable
    data object TokenizationSeparate : Route()
}
