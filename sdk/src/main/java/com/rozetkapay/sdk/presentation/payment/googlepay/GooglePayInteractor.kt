package com.rozetkapay.sdk.presentation.payment.googlepay

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.rozetkapay.sdk.util.Logger
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

internal class GooglePayInteractor(
    applicationContext: Context,
    gateway: String,
    merchantId: String,
    merchantName: String,
    private val countryCode: String,
    isTestEnvironment: Boolean = false,
) {
    private val gatewayTokenizationSpecification: JSONObject =
        JSONObject()
            .put("type", "PAYMENT_GATEWAY")
            .put(
                "parameters",
                JSONObject(
                    mapOf(
                        "gateway" to gateway,
                        "gatewayMerchantId" to merchantId
                    )
                )
            )

    private val cardPaymentMethod: JSONObject = GooglePayUtils.baseCardPaymentMethod
        .put("tokenizationSpecification", gatewayTokenizationSpecification)
    private val merchantInfo: JSONObject = JSONObject().put("merchantName", merchantName)
    private val environment: Int = if (isTestEnvironment) {
        WalletConstants.ENVIRONMENT_TEST
    } else {
        WalletConstants.ENVIRONMENT_PRODUCTION
    }

    private val paymentsClient: PaymentsClient = createPaymentsClient(applicationContext)

    suspend fun fetchCanUseGooglePay(): Boolean {
        val requestData = try {
            GooglePayUtils.baseRequest
                .put(
                    "allowedPaymentMethods",
                    JSONArray().put(GooglePayUtils.baseCardPaymentMethod)
                )
        } catch (e: JSONException) {
            null
        }
        val request = IsReadyToPayRequest.fromJson(requestData.toString())
        return paymentsClient.isReadyToPay(request).await()
    }

    fun getAllowedPaymentMethods(): JSONArray {
        return JSONArray().put(cardPaymentMethod)
    }

    fun preparePaymentTask(
        priceCoins: Long,
        currencyCode: String,
    ): Task<PaymentData> {
        val paymentDataRequestJson = getPaymentDataRequest(
            priceCoins = priceCoins,
            currencyCode = currencyCode,
            countryCode = countryCode
        )
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    private fun createPaymentsClient(context: Context): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(environment)
            .build()
        return Wallet.getPaymentsClient(context, walletOptions)
    }

    private fun getPaymentDataRequest(
        priceCoins: Long,
        currencyCode: String,
        countryCode: String,
    ): JSONObject =
        GooglePayUtils.baseRequest
            .put("allowedPaymentMethods", getAllowedPaymentMethods())
            .put(
                "transactionInfo",
                JSONObject()
                    .put("totalPrice", priceCoins.coinsToString())
                    .put("totalPriceStatus", "FINAL")
                    .put("countryCode", countryCode)
                    .put("currencyCode", currencyCode)
            )
            .put("merchantInfo", merchantInfo)
            .put("shippingAddressRequired", false)

    private fun Long.coinsToString() = BigDecimal(this)
        .divide(BigDecimal(100))
        .setScale(2, RoundingMode.HALF_EVEN)
        .toString()

    fun extractToken(paymentData: PaymentData): String? {
        return try {
            JSONObject(paymentData.toJson())
                .getJSONObject("paymentMethodData")
                .getJSONObject("tokenizationData")
                .getString("token")
        } catch (e: Exception) {
            Logger.e(throwable = e) { "Failed to extract token from Google Pay result" }
            null
        }
    }
}