package com.rozetkapay.sdk.presentation.payment.googlepay

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton
import com.rozetkapay.sdk.domain.models.payment.GooglePayConfig
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import com.rozetkapay.sdk.presentation.util.withResourceId

/**
 * A standalone, Google-branded Pay button that can be placed anywhere in the host app's own
 * UI. This composable is UI-only — it renders nothing if Google Pay isn't available for the
 * current device, and otherwise just reports taps via [onClick].
 *
 * To actually process a payment when the button is tapped, call `.show(...)` on your existing
 * [com.rozetkapay.sdk.presentation.payment.regular.PaymentSheet] or
 * [com.rozetkapay.sdk.presentation.payment.batch.BatchPaymentSheet] from [onClick], with
 * `paymentType` set to [com.rozetkapay.sdk.domain.models.payment.GooglePayPayment]. That
 * launches the exact same flow (including 3DS confirmation) as picking Google Pay from inside
 * the sheet, just skipping straight past the card-form / payment-method-selection UI.
 *
 * @param googlePayConfig configuration required to check Google Pay availability and render the button
 * @param onClick called when the user taps the button
 */
@Composable
fun GooglePayButton(
    modifier: Modifier = Modifier,
    googlePayConfig: GooglePayConfig,
    themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    onClick: () -> Unit,
) {
    RozetkaPayTheme(
        themeConfigurator = themeConfigurator
    ) {
        val readiness = rememberGooglePayReadiness(googlePayConfig)
        if (readiness.isReady) {
            PayButton(
                modifier = modifier
                    .withResourceId("buttonGooglePayStandalone")
                    .fillMaxWidth()
                    .height(DomainTheme.sizes.googlePayButtonHeight),
                onClick = onClick,
                type = ButtonType.Buy,
                theme = if (isSystemInDarkTheme()) ButtonTheme.Light else ButtonTheme.Dark,
                radius = DomainTheme.sizes.buttonCornerRadius,
                allowedPaymentMethods = readiness.allowedPaymentMethodsJson,
            )
        }
    }
}

private data class GooglePayReadiness(
    val isReady: Boolean,
    val allowedPaymentMethodsJson: String,
)

@Composable
private fun rememberGooglePayReadiness(
    googlePayConfig: GooglePayConfig,
): GooglePayReadiness {
    if (LocalInspectionMode.current) {
        return remember { GooglePayReadiness(isReady = true, allowedPaymentMethodsJson = "") }
    }
    val applicationContext = LocalContext.current.applicationContext
    val interactor = remember(googlePayConfig) {
        googlePayConfig.toInteractor(applicationContext)
    }
    var readiness by remember(interactor) {
        mutableStateOf(GooglePayReadiness(isReady = false, allowedPaymentMethodsJson = ""))
    }
    LaunchedEffect(interactor) {
        val isReady = interactor.fetchCanUseGooglePay()
        readiness = GooglePayReadiness(
            isReady = isReady,
            allowedPaymentMethodsJson = if (isReady) {
                interactor.getAllowedPaymentMethods().toString()
            } else {
                ""
            },
        )
    }
    return readiness
}

@Preview(showBackground = true)
@Preview(
    backgroundColor = 0xFF000000,
    showBackground = true, uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun GooglePayButtonPreview() {
    GooglePayButton(
        googlePayConfig = GooglePayConfig.Test(
            merchantId = "preview_merchant_id",
        ),
        onClick = {},
    )
}
