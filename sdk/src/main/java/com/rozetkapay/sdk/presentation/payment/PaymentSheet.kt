package com.rozetkapay.sdk.presentation.payment

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.presentation.payment.launcher.DefaultPaymentSheetLauncher
import com.rozetkapay.sdk.presentation.payment.launcher.PaymentSheetLauncher
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

@Composable
fun rememberPaymentSheet(
    onResultCallback: PaymentSheetResultCallback,
): PaymentSheet {
    val resultCallback by rememberUpdatedState(newValue = onResultCallback::onPaymentSheetResult)
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract(),
        onResult = resultCallback,
    )
    val context = LocalContext.current
    return remember(onResultCallback) {
        val launcher = DefaultPaymentSheetLauncher(
            activityResultLauncher = activityResultLauncher,
            application = context.applicationContext as Application,
            callback = onResultCallback,
        )
        PaymentSheet(
            launcher = launcher
        )
    }
}

class PaymentSheet internal constructor(
    private val launcher: PaymentSheetLauncher,
) {
    constructor(
        activity: ComponentActivity,
        callback: PaymentSheetResultCallback,
    ) : this(
        DefaultPaymentSheetLauncher(activity, callback)
    )

    constructor(
        fragment: Fragment,
        callback: PaymentSheetResultCallback,
    ) : this(
        DefaultPaymentSheetLauncher(fragment, callback)
    )

    fun show(
        client: ClientParameters,
        parameters: PaymentParameters,
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    ) {
        launcher.present(
            client = client,
            parameters = parameters,
            themeConfigurator = themeConfigurator,
        )
    }
}
