package com.rozetkapay.demo.presentation.tokenization

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoClassicTheme
import com.rozetkapay.demo.presentation.theme.classicRozetkaPaySdkThemeConfigurator
import com.rozetkapay.sdk.domain.models.FieldRequirement
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.presentation.tokenization.TokenizationSheet

class TokenizationActivity : ComponentActivity() {
    
    private val viewModel: CardsViewModel by viewModels()
    private lateinit var tokenizationSheet: TokenizationSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tokenizationSheet = TokenizationSheet(
            activity = this,
            callback = viewModel::tokenizationFinished
        )

        setContent {
            RozetkaPayDemoClassicTheme {
                val cards by viewModel.cards.collectAsStateWithLifecycle()
                TokenizationScreenContent(
                    cards = cards,
                    errorsFlow = viewModel.errorEventsFlow,
                    onBack = { finish() },
                    onTokenizeClick = {
                        tokenizationSheet.show(
                            client = viewModel.clientWidgetParameters,
                            parameters = TokenizationParameters(
                                cardNameField = FieldRequirement.None
                            ),
                            themeConfigurator = classicRozetkaPaySdkThemeConfigurator
                        )
                    }
                )
            }
        }
    }

    companion object {
        fun startIntent(context: Context) = Intent(context, TokenizationActivity::class.java)
    }
}