package com.rozetkapay.sdk.init

import com.rozetkapay.sdk.domain.validators.rules.CardExpirationDateValidationRule
import com.rozetkapay.sdk.domain.validators.rules.DefaultCardExpirationDateValidationRule

data class RozetkaPaySdkValidationRules(
    val cardExpirationDateValidationRule: CardExpirationDateValidationRule =
        RozetkaPaySdkValidationRulesDefaults.cardExpirationDateValidationRule,
)

object RozetkaPaySdkValidationRulesDefaults {
    val cardExpirationDateValidationRule: CardExpirationDateValidationRule =
        DefaultCardExpirationDateValidationRule
}