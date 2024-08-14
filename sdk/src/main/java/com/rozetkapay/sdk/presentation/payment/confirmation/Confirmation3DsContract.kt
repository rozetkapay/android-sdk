package com.rozetkapay.sdk.presentation.payment.confirmation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator
import kotlinx.parcelize.Parcelize

internal class Confirmation3DsContract :
    ActivityResultContract<Confirmation3DsContract.Parameters, ConfirmPaymentResult>() {

    override fun createIntent(context: Context, input: Parameters): Intent {
        return Intent(context, Confirmation3DsActivity::class.java).putExtra(EXTRA_PARAMS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ConfirmPaymentResult {
        @Suppress("DEPRECATION")
        val result = intent?.getParcelableExtra<Result>(EXTRA_RESULT)?.confirmResult
        return result ?: ConfirmPaymentResult.Cancelled
    }

    @Parcelize
    data class Parameters(
        val paymentId: String,
        val url: String,
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
        val confirmResult: ConfirmPaymentResult,
    ) : Parcelable {
        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    internal companion object {
        private const val EXTRA_PARAMS = "com.rozetkapay.sdk.Confirmation3dsContract.parameters"
        private const val EXTRA_RESULT = "com.rozetkapay.sdk.Confirmation3dsContract.result"
    }
}