package com.rozetkapay.sdk.domain.usecases

import android.content.Context
import com.rozetkapay.sdk.R

interface TokenizationStringResourcesProvider {
    val saveButtonTitle: String
}

class DefaultTokenizationStringResourcesProvider(
    val context: Context,
) : TokenizationStringResourcesProvider {
    override val saveButtonTitle: String
        get() = context.getString(R.string.rozetka_pay_tokenization_save_button)
}