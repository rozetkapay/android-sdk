package com.rozetkapay.sdk.presentation.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.rozetkapay.sdk.domain.models.ClientParameters
import com.rozetkapay.sdk.domain.models.ClientPayParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.parcelize.Parcelize

class PaymentSheetContract :
    ActivityResultContract<PaymentSheetContract.Parameters, PaymentResult>() {

    override fun createIntent(context: Context, input: Parameters): Intent {
        return Intent(context, PaymentSheetActivity::class.java).putExtra(EXTRA_PARAMS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PaymentResult {
        @Suppress("DEPRECATION")
        val result = intent?.getParcelableExtra<Result>(EXTRA_RESULT)?.paymentResult
        return result ?: PaymentResult.Failed(
            error = IllegalArgumentException("Failed to retrieve a PaymentResult.")
        )
    }

    @Parcelize
    data class Parameters(
        val client: ClientPayParameters,
        val parameters: PaymentParameters,
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
        val paymentResult: PaymentResult,
    ) : Parcelable {
        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    internal companion object {
        private const val EXTRA_PARAMS = "com.rozetkapay.sdk.PaymentSheetContract.parameters"
        private const val EXTRA_RESULT = "com.rozetkapay.sdk.PaymentSheetContract.result"
    }
}
