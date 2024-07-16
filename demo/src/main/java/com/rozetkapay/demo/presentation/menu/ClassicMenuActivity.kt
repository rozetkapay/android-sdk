package com.rozetkapay.demo.presentation.menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoClassicTheme
import com.rozetkapay.demo.presentation.tokenization.TokenizationSheetActivity

class ClassicMenuActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RozetkaPayDemoClassicTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { _ ->
                    MenuScreen(
                        onNavigationEvent = { route ->
                            when (route) {
                                Route.Menu -> {
                                    // skip
                                }

                                Route.TokenizationSeparate -> {
                                    startActivity(TokenizationSheetActivity.startIntent(this))
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, ClassicMenuActivity::class.java)
    }
}
