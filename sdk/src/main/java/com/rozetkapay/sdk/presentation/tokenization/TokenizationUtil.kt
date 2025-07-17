package com.rozetkapay.sdk.presentation.tokenization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.rozetkapay.sdk.domain.usecases.DefaultTokenizationStringResourcesProvider
import com.rozetkapay.sdk.domain.usecases.TokenizationStringResourcesProvider

@Composable
fun rememberDefaultTokenizationStringResourcesProvider(): TokenizationStringResourcesProvider {
    val context = LocalContext.current
    return remember(context) {
        DefaultTokenizationStringResourcesProvider(
            context = context
        )
    }
}