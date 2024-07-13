package com.rozetkapay.sdk.presentation.tokenization.launcher

import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

internal interface TokenizationSheetLauncher {
    fun present(
        client: ClientParameters,
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    )
}
