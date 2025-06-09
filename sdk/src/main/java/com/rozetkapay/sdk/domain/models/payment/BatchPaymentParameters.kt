package com.rozetkapay.sdk.domain.models.payment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Parameters for batch payment.
 * Batch payment is a payment with multiple orders in one transaction.
 * Each order will be processed separately, but all orders will be paid in one transaction with single amount.
 *
 * @property currencyCode ISO-4217 currency code for the payment
 * @property externalId Unique external id of the batch payment in your system
 * @property callbackUrl Optional callback URL that will be called after the payment is finished
 * @property paymentType Configuration for the payment type, default is [RegularPayment]
 * @property orders List of orders to be paid in the batch payment
 */
@Parcelize
data class BatchPaymentParameters(
    val currencyCode: String,
    val externalId: String,
    val callbackUrl: String? = null,
    val paymentType: PaymentTypeConfiguration = RegularPayment(),
    val orders: List<Order>,
) : Parcelable {

    /**
     * Represents an order in a batch payment.
     *
     * @property apiKey API key of the merchant who will receive payment for this order
     * @property amount Amount of the order in coins
     * @property externalId Unique external id for the order in your system (for example can be orderId)
     * @property description Description of the order, can be used for additional information, CAN'T BE EMPTY
     */
    @Parcelize
    data class Order(
        val apiKey: String,
        val amount: Long,
        val externalId: String,
        val description: String
    ) : Parcelable
}