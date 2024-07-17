package com.rozetkapay.sdk.domain.validators

import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.CardExpDate
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class CardExpDateValidator(
    private val resourcesProvider: ResourcesProvider,
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
        val currentYear: Int = localDate.year % 100
        val currentMonth: Int = localDate.monthNumber

        return if (value.year < currentYear || (value.year == currentYear && value.month < currentMonth)) {
            ValidationResult.Error(
                resourcesProvider.getString(R.string.rozetka_pay_form_validation_exp_date_expired)
            )
        } else {
            ValidationResult.Valid
        }
    }
}

