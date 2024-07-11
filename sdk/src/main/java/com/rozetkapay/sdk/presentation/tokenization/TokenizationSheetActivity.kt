package com.rozetkapay.sdk.presentation.tokenization

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.domain.models.PaymentSystem
import com.rozetkapay.sdk.domain.models.TokenizationResult
import com.rozetkapay.sdk.domain.models.TokenizedCard
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity

internal class TokenizationSheetActivity : BaseRozetkaPayActivity() {

    private val parameters: TokenizationSheetContract.Parameters? by lazy {
        TokenizationSheetContract.Parameters.fromIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback {
            setActivityResult(
                TokenizationResult.Cancelled
            )
            finish()
        }
        setContent {
            TokenizationContent(
                onSuccess = {
                    setActivityResult(
                        TokenizationResult.Complete(
                            tokenizedCard = TokenizedCard(
                                token = "demotoken",
                                maskedNumber = "**** **** **** 4242",
                                PaymentSystem.Visa,
                                name = "New card for client ${parameters?.client?.key}"
                            )
                        )
                    )
                    finish()
                },
                onFailure = {
                    setActivityResult(
                        TokenizationResult.Failed()
                    )
                    finish()
                }
            )
        }
    }

    private fun setActivityResult(result: TokenizationResult) {
        setResult(
            RESULT_OK,
            Intent().putExtras(
                TokenizationSheetContract.Result(
                    tokenizationResult = result
                ).toBundle()
            )
        )
    }
}

@Composable
private fun TokenizationContent(
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.run {
            spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = "Tokenization bottom sheet",
            modifier = Modifier.fillMaxWidth(),
        )
        BasicText(
            text = "In development",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(32.dp))
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .padding(16.dp)
                .clickable {
                    onSuccess()
                },
            text = "Success button"
        )
        BasicText(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .padding(16.dp)
                .clickable {
                    onFailure()
                },
            text = "Failure button"
        )
    }
}

@Composable
@Preview
private fun TokenizationContentPreview() {
    TokenizationContent(
        onSuccess = {},
        onFailure = {},
    )
}
