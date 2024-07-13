package com.rozetkapay.sdk.presentation.tokenization

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationParameters
import com.rozetkapay.sdk.domain.models.tokenization.TokenizationResult
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.parcelize.Parcelize

class TokenizationSheetContract :
    ActivityResultContract<TokenizationSheetContract.Parameters, TokenizationResult>() {

    override fun createIntent(context: Context, input: Parameters): Intent {
        return Intent(context, TokenizationSheetActivity::class.java).putExtra(EXTRA_PARAMS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): TokenizationResult {
        @Suppress("DEPRECATION")
        val result = intent?.getParcelableExtra<Result>(EXTRA_RESULT)?.tokenizationResult
        return result ?: TokenizationResult.Failed(
            IllegalArgumentException("Failed to retrieve a TokenizationResult.")
        )
    }

    @Parcelize
    data class Parameters(
        val client: ClientParameters,
        val parameters: TokenizationParameters = TokenizationParameters(),
        val themeConfigurator: RozetkaPayThemeConfigurator = RozetkaPayThemeConfigurator(),
    ) : Parcelable {

        companion object {
            internal fun fromIntent(intent: Intent): Parameters? {
                @Suppress("DEPRECATION")
                return intent.getParcelableExtra(EXTRA_PARAMS)
            }
        }
    }

    @Parcelize
    data class Result(
        val tokenizationResult: TokenizationResult,
    ) : Parcelable {
        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    internal companion object {
        private const val EXTRA_PARAMS = "com.rozetkapay.sdk.TokenizationSheetContract.parameters"
        private const val EXTRA_RESULT = "com.rozetkapay.sdk.TokenizationSheetContract.result"
    }
}
