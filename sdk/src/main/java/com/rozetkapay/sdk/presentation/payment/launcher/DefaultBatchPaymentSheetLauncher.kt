package com.rozetkapay.sdk.presentation.payment.launcher

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentResult
import com.rozetkapay.sdk.presentation.payment.batch.BatchPaymentResultCallback
import com.rozetkapay.sdk.presentation.payment.batch.BatchPaymentSheetContract
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import com.rozetkapay.sdk.presentation.util.RozetkaPayAnimations

internal class DefaultBatchPaymentSheetLauncher(
    private val activityResultLauncher: ActivityResultLauncher<BatchPaymentSheetContract.Parameters>,
    private val application: Application,
    private val callback: BatchPaymentResultCallback,
) : BatchPaymentSheetLauncher {

    constructor(
        activity: ComponentActivity,
        callback: BatchPaymentResultCallback,
    ) : this(
        activityResultLauncher = activity.registerForActivityResult(BatchPaymentSheetContract()) {
            callback.onPaymentResult(it)
        },
        application = activity.application,
        callback = callback,
    )

    constructor(
        fragment: Fragment,
        callback: BatchPaymentResultCallback,
    ) : this(
        activityResultLauncher = fragment.registerForActivityResult(BatchPaymentSheetContract()) {
            callback.onPaymentResult(it)
        },
        application = fragment.requireActivity().application,
        callback = callback,
    )

    override fun present(
        clientAuthParameters: ClientAuthParameters,
        parameters: BatchPaymentParameters,
        themeConfigurator: RozetkaPayThemeConfigurator,
    ) {
        val contractParameters = BatchPaymentSheetContract.Parameters(
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
            callback.onPaymentResult(BatchPaymentResult.Failed(error = error))
        }
    }
}
