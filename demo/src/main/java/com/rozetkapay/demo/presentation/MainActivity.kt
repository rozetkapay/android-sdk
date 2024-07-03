package com.rozetkapay.demo.presentation

import android.annotation.SuppressLint
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
import com.rozetkapay.demo.presentation.menu.MenuScreen
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import com.rozetkapay.demo.presentation.tokenization.TokenizationBuiltInScreen
import com.rozetkapay.demo.presentation.tokenization.TokenizationSeparateScreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
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
                                onNavigationEvent = { route ->
                                    navController.navigate(route)
                                }
                            )
                        }
                        composable<Route.TokenizationBuildIn> {
                            TokenizationBuiltInScreen(
                                onBack = { navController.navigateUp() }
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
}

sealed class Route {
    @Serializable
    data object Menu : Route()

    @Serializable
    data object TokenizationBuildIn : Route()

    @Serializable
    data object TokenizationSeparate : Route()
}