package com.rozetkapay.sdk.presentation.payment.batch

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.presentation.payment.launcher.BatchPaymentSheetLauncher
import com.rozetkapay.sdk.presentation.payment.launcher.DefaultBatchPaymentSheetLauncher
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

@Composable
fun rememberBatchPaymentSheet(
    onResultCallback: BatchPaymentResultCallback,
): BatchPaymentSheet {
    val resultCallback by rememberUpdatedState(newValue = onResultCallback::onPaymentResult)
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = BatchPaymentSheetContract(),
        onResult = resultCallback,
    )
    val context = LocalContext.current
    return remember(onResultCallback) {
        val launcher = DefaultBatchPaymentSheetLauncher(
            activityResultLauncher = activityResultLauncher,
            application = context.applicationContext as Application,
            callback = onResultCallback,
        )
        BatchPaymentSheet(
            launcher = launcher
        )
    }
}

@Suppress("unused")
class BatchPaymentSheet internal constructor(
    private val launcher: BatchPaymentSheetLauncher,
) {
    constructor(
        activity: ComponentActivity,
        callback: BatchPaymentResultCallback,
    ) : this(
        DefaultBatchPaymentSheetLauncher(activity, callback)
    )

    constructor(
        fragment: Fragment,
        callback: BatchPaymentResultCallback,
    ) : this(
        DefaultBatchPaymentSheetLauncher(fragment, callback)
    )

    fun show(
        clientAuthParameters: ClientAuthParameters,
        parameters: BatchPaymentParameters,
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    ) {
        launcher.present(
            clientAuthParameters = clientAuthParameters,
            parameters = parameters,
            themeConfigurator = themeConfigurator,
        )
    }
}
