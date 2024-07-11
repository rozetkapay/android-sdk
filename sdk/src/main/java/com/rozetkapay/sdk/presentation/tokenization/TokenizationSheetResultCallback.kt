package com.rozetkapay.sdk.presentation.tokenization

import com.rozetkapay.sdk.domain.models.TokenizationResult

fun interface TokenizationSheetResultCallback {
    fun onTokenizationSheetResult(result: TokenizationResult)
}
