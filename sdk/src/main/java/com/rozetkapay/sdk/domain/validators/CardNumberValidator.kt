package com.rozetkapay.sdk.domain.validators

import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.repository.ResourcesProvider

internal class CardNumberValidator(
    private val resourcesProvider: ResourcesProvider,
) : Validator<String?>() {
    override fun validate(value: String?): ValidationResult {
        val isValid = !value.isNullOrBlank()
            && value.matches("^[0-9]+$".toRegex())
            && value.length >= 16
            && validateCardNumberWithLuhnAlgorithm(value)
        return if (isValid) ValidationResult.Valid else generateError()
    }

    private fun generateError(): ValidationResult.Error {
        return ValidationResult.Error(
            resourcesProvider.getString(R.string.rozetka_pay_form_validation_card_number_incorrect)
        )
    }

    private fun validateCardNumberWithLuhnAlgorithm(cardNumber: String): Boolean {
        val digits = cardNumber
            .map { Character.getNumericValue(it) }
            .toMutableList()
        for (i in (digits.size - 2) downTo 0 step 2) {
            var value = digits[i] * 2
            if (value > 9) {
                value = value % 10 + 1
            }
            digits[i] = value
        }
        return digits.sum() % 10 == 0
    }
}

