package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BatchOrderPaymentResult(
    val externalId: String,
    val operationId: String,
) : Parcelable