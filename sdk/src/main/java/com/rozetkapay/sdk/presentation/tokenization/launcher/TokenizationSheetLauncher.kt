package com.rozetkapay.sdk.presentation.tokenization.launcher

import com.rozetkapay.sdk.domain.models.ClientParameters

interface TokenizationSheetLauncher {
    fun present(client: ClientParameters)
}
