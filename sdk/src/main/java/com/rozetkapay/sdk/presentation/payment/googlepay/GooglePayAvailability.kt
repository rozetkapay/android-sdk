package com.rozetkapay.sdk.presentation.payment.googlepay

import android.content.Context
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig

/**
 * Checks whether Google Pay can be used on this device with the given configuration.
 * Use this before rendering your own Google Pay button (e.g. outside of Compose,
 * or from a native bridge) to decide whether it's correct to show it at all.
 */
suspend fun isGooglePayAvailable(
    context: Context,
    googlePayConfig: GooglePayConfig,
): Boolean {
    val interactor = googlePayConfig.toInteractor(context.applicationContext)
    return interactor.fetchCanUseGooglePay()
}
