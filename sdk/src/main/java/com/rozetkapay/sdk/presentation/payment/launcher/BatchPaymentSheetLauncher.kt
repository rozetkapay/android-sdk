package com.rozetkapay.sdk.presentation.payment.launcher

import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

internal interface BatchPaymentSheetLauncher {
    fun present(
        clientAuthParameters: ClientAuthParameters,
        parameters: BatchPaymentParameters,
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    )
}
