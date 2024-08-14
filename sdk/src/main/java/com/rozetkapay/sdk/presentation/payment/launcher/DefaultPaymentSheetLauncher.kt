package com.rozetkapay.sdk.presentation.payment.launcher

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.presentation.payment.PaymentSheetContract
import com.rozetkapay.sdk.presentation.payment.PaymentSheetResultCallback
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import com.rozetkapay.sdk.presentation.util.RozetkaPayAnimations

internal class DefaultPaymentSheetLauncher(
    private val activityResultLauncher: ActivityResultLauncher<PaymentSheetContract.Parameters>,
    private val application: Application,
    private val callback: PaymentSheetResultCallback,
) : PaymentSheetLauncher {

    constructor(
        activity: ComponentActivity,
        callback: PaymentSheetResultCallback,
    ) : this(
        activityResultLauncher = activity.registerForActivityResult(PaymentSheetContract()) {
            callback.onPaymentSheetResult(it)
        },
        application = activity.application,
        callback = callback,
    )

    constructor(
        fragment: Fragment,
        callback: PaymentSheetResultCallback,
    ) : this(
        activityResultLauncher = fragment.registerForActivityResult(PaymentSheetContract()) {
            callback.onPaymentSheetResult(it)
        },
        application = fragment.requireActivity().application,
        callback = callback,
    )

    override fun present(
        clientAuthParameters: ClientAuthParameters,
        parameters: PaymentParameters,
        themeConfigurator: RozetkaPayThemeConfigurator,
    ) {
        val contractParameters = PaymentSheetContract.Parameters(
            clientAuthParameters = clientAuthParameters,
            parameters = parameters,
            themeConfigurator = themeConfigurator,
        )
        val options = ActivityOptionsCompat.makeCustomAnimation(
            application.applicationContext,
            RozetkaPayAnimations.fadeIn,
            RozetkaPayAnimations.fadeOut,
        )
        try {
            activityResultLauncher.launch(contractParameters, options)
        } catch (e: IllegalStateException) {
            val error = IllegalStateException("The host activity is not in a valid state", e)
            callback.onPaymentSheetResult(PaymentResult.Failed(error = error))
        }
    }
}
