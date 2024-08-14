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
    data class Test(
        override val merchantId: String,
        override val merchantName: String = "RozetkaPay Test Merchant",
        override val gateway: String = RozetkaPayConfig.GOOGLE_PAY_GATEWAY,
    ) : GooglePayConfig(
        merchantId = merchantId,
        merchantName = merchantName,
        gateway = gateway
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