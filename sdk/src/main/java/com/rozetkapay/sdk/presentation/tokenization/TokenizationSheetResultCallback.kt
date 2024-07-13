package com.rozetkapay.sdk.presentation.tokenization

import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult

fun interface TokenizationSheetResultCallback {
    fun onTokenizationSheetResult(result: TokenizationResult)
}
