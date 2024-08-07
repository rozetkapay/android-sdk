package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentParameters(
    val allowTokenization: Boolean = true,
) : Parcelable
