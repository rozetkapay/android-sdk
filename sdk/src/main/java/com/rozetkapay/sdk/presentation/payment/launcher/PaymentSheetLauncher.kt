package com.rozetkapay.sdk.presentation.payment.launcher

import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

internal interface PaymentSheetLauncher {
    fun present(
        clientAuthParameters: ClientAuthParameters,
        parameters: PaymentParameters,
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    )
}
