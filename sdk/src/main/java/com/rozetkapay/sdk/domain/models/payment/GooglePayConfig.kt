package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import com.rozetkapay.sdk.domain.RozetkaPayConfig
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class GooglePayConfig(
    open val gateway: String,
    open val merchantId: String,
    open val merchantName: String,
) : Parcelable {

    @Parcelize
    data object Test : GooglePayConfig(
        // predefined test parameters provided by Google
        // https://developers.google.com/pay/api/android/guides/tutorial
        gateway = "example",
        merchantId = "exampleGatewayMerchantId",
        merchantName = "RozetkaPay Test Merchant"
    )

    @Parcelize
    data class Production(
        override val merchantId: String,
        override val merchantName: String,
    ) : GooglePayConfig(
        gateway = RozetkaPayConfig.GOOGLE_PAY_GATEWAY,
        merchantId = merchantId,
        merchantName = merchantName
    )
}