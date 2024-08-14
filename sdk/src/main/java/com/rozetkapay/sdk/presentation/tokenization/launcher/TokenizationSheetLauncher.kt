package com.rozetkapay.sdk.presentation.tokenization.launcher

import com.rozetkapay.sdk.domain.models.ClientWidgetParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

internal interface TokenizationSheetLauncher {
    fun present(
        client: ClientWidgetParameters,
        parameters: TokenizationParameters,
        themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    )
}
