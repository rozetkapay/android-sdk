package com.rozetkapay.sdk.domain.models.tokenization

import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.usecases.TokenizationStringResourcesProvider

data class TokenizationFormParameters(
    val cardFieldsParameters: CardFieldsParameters = CardFieldsParameters(),
    val stringResourcesProvider: TokenizationStringResourcesProvider? = null,
    val showCardFormTitle: Boolean = true,
    val showLegalBlock: Boolean = true,
)