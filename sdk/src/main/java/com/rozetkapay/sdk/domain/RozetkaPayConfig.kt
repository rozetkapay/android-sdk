package com.rozetkapay.sdk.domain

object RozetkaPayConfig {
    const val LEGAL_PUBLIC_CONTRACT_LINK = "https://drive.google.com/file/d/1CRg5UjDKvWLST5VCFkHB1Btv_FmomK8h/view"
    const val LEGAL_COMPANY_DETAILS_LINK = "https://rozetkapay.com/legal-info/perekaz-koshtiv/"

    const val GOOGLE_PAY_GATEWAY = "evopay"
    const val GOOGLE_PAY_COUNTRY_CODE = "UA"

    // TODO: temporary values for local testing
    // should be replaced with real values before release
    const val PAYMENT_3DS_CALLBACK_URL_SUCCESS = "http://10.10.11.185:3000/links/transfers/success"
    const val PAYMENT_3DS_CALLBACK_URL_ERROR = "http://10.10.11.185:3000/links/transfers/error"
}