package com.rozetkapay.sdk.domain

import com.rozetkapay.sdk.domain.models.RozetkaPayEnvironment

internal object RozetkaPayConfig {
    val devEnvironment = RozetkaPayEnvironment(
        tokenizationApiProviderUrl = "https://widget-epdev.rozetkapay.com",
        paymentsApiProviderUrl = "https://api-epdev.rozetkapay.com",
        paymentsConfirmation3DsCallbackUrl = "https://checkout-epdev.rozetkapay.com"
    )
    val prodEnvironment = RozetkaPayEnvironment(
        tokenizationApiProviderUrl = "https://widget.rozetkapay.com",
        paymentsApiProviderUrl = "https://api.rozetkapay.com",
        paymentsConfirmation3DsCallbackUrl = "https://checkout.rozetkapay.com"
    )

    const val LEGAL_PUBLIC_CONTRACT_LINK = "https://drive.google.com/file/d/1CRg5UjDKvWLST5VCFkHB1Btv_FmomK8h/view"
    const val LEGAL_COMPANY_DETAILS_LINK = "https://rozetkapay.com/legal-info/perekaz-koshtiv/"

    const val GOOGLE_PAY_GATEWAY = "evopay"
    const val GOOGLE_PAY_COUNTRY_CODE = "UA"
}