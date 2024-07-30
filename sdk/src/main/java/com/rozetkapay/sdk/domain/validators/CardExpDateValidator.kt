package com.rozetkapay.sdk.domain.validators

import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.CardExpDate
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.validators.rules.CardExpirationDateValidationRule
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class CardExpDateValidator(
    private val resourcesProvider: ResourcesProvider,
    private val expirationValidationRule: CardExpirationDateValidationRule,
    private val currentLocalDateProvider: () -> LocalDate = {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    },
) : Validator<CardExpDate?>() {
    override fun validate(value: CardExpDate?): ValidationResult {
        if (value == null || value.month < 1 || value.month > 12) {
            return ValidationResult.Error(
                resourcesProvider.getString(R.string.rozetka_pay_form_validation_exp_date_invalid)
            )
        }
        val localDate = currentLocalDateProvider()
        return when (expirationValidationRule.validate(
            currentDate = localDate,
            expYear = value.year,
            expMonth = value.month
        )) {
            true -> ValidationResult.Valid
            false -> ValidationResult.Error(
                resourcesProvider.getString(R.string.rozetka_pay_form_validation_exp_date_expired)
            )
        }
    }
}