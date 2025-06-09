package com.rozetkapay.sdk.presentation.payment.regular

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.wallet.contract.TaskResultContracts
import com.rozetkapay.sdk.domain.models.CardFieldsParameters
import com.rozetkapay.sdk.domain.models.payment.PaymentResult
import com.rozetkapay.sdk.domain.models.payment.RegularPayment
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity
import com.rozetkapay.sdk.presentation.forms.card.CardFormViewModel
import com.rozetkapay.sdk.presentation.payment.PaymentAction
import com.rozetkapay.sdk.presentation.payment.confirmation.Confirmation3DsContract
import com.rozetkapay.sdk.presentation.theme.RozetkaPayThemeConfigurator

internal class PaymentSheetActivity : BaseRozetkaPayActivity() {

    private val parameters: PaymentSheetContract.Parameters? by lazy {
        PaymentSheetContract.Parameters.fromIntent(intent)
    }

    @VisibleForTesting
    internal var viewModelFactory: ViewModelProvider.Factory = PaymentViewModel.Factory {
        requireNotNull(parameters)
    }

    private val viewModel: PaymentViewModel by viewModels { viewModelFactory }

    @VisibleForTesting
    internal var cardFormViewModelFactory: ViewModelProvider.Factory = CardFormViewModel.Factory {
        (parameters?.parameters?.paymentType as? RegularPayment)?.cardFieldsParameters ?: CardFieldsParameters()
    }

    private val cardFormViewModel: CardFormViewModel by viewModels { cardFormViewModelFactory }

    private val googlePayDataLauncher =
        registerForActivityResult(TaskResultContracts.GetPaymentDataResult()) { result ->
            viewModel.onAction(PaymentAction.GooglePayResult(result))
        }
    private val confirm3DsDataLauncher =
        registerForActivityResult(Confirmation3DsContract()) { result ->
            viewModel.onAction(PaymentAction.PaymentConfirmed(result))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeConfigurator = parameters?.themeConfigurator ?: RozetkaPayThemeConfigurator()
        setContent {
            RegularPaymentBottomSheet(
                viewModel = viewModel,
                cardFormViewModel = cardFormViewModel,
                themeConfigurator = themeConfigurator,
                onAction = ::onPaymentBottomSheetAction
            )
        }
    }

    private fun onPaymentBottomSheetAction(action: RegularPaymentBottomSheetAction) {
        when (action) {
            RegularPaymentBottomSheetAction.Finish -> {
                finish()
            }

            is RegularPaymentBottomSheetAction.Launch3ds -> {
                confirm3DsDataLauncher.launch(action.params)
            }

            is RegularPaymentBottomSheetAction.LaunchGooglePay -> {
                action.task.addOnCompleteListener(
                    googlePayDataLauncher::launch
                )
            }

            is RegularPaymentBottomSheetAction.SetResult -> {
                setActivityResult(action.result)
            }
        }
    }

    private fun setActivityResult(result: PaymentResult) {
        setResult(
            RESULT_OK,
            Intent().putExtras(
                PaymentSheetContract.Result(
                    paymentResult = result
                ).toBundle()
            )
        )
    }
}
