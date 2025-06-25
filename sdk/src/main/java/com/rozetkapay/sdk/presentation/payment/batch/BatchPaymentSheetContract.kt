package com.rozetkapay.sdk.presentation.payment.batch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.rozetkapay.sdk.domain.models.ClientAuthParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentParameters
import com.rozetkapay.sdk.domain.models.payment.BatchPaymentResult
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.parcelize.Parcelize

class BatchPaymentSheetContract :
    ActivityResultContract<BatchPaymentSheetContract.Parameters, BatchPaymentResult>() {

    override fun createIntent(context: Context, input: Parameters): Intent {
        return Intent(context, BatchPaymentSheetActivity::class.java).putExtra(EXTRA_PARAMS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): BatchPaymentResult {
        @Suppress("DEPRECATION")
        val result = intent?.getParcelableExtra<Result>(EXTRA_RESULT)?.paymentResult
        return result ?: BatchPaymentResult.Failed(
            error = IllegalArgumentException("Failed to retrieve a BatchPaymentResult.")
        )
    }

    @Parcelize
    data class Parameters(
        val clientAuthParameters: ClientAuthParameters,
        val parameters: BatchPaymentParameters,
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
        val paymentResult: BatchPaymentResult,
    ) : Parcelable {
        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    internal companion object {
        private const val EXTRA_PARAMS = "com.rozetkapay.sdk.BatchPaymentSheetContract.parameters"
        private const val EXTRA_RESULT = "com.rozetkapay.sdk.BatchPaymentSheetContract.result"
    }
}
