package com.rozetkapay.demo.presentation.payment

/**
 * The payment demo screens let a user pick between three checkout options; used only to
 * pick which [com.rozetkapay.sdk.domain.models.payment.PaymentTypeConfiguration] to launch
 * the payment sheet with.
 */
enum class CheckoutOption {
    Card,
    TokenizedCard,
    GooglePay,
}
