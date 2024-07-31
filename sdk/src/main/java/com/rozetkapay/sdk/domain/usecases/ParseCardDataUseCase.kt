package com.rozetkapay.sdk.domain.usecases

import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.CardData
import com.rozetkapay.sdk.domain.models.CardExpDate
import com.rozetkapay.sdk.domain.models.CardExpirationDateException
import com.rozetkapay.sdk.domain.repository.ResourcesProvider
import com.rozetkapay.sdk.domain.validators.ValidationResult
import com.rozetkapay.sdk.domain.validators.Validator

internal class ParseCardDataUseCase(
    private val cardNumberValidator: Validator<String>,
    private val cvvValidator: Validator<String>,
    private val expDateValidator: Validator<CardExpDate>,
    private val cardholderNameValidator: Validator<String>,
    private val resourcesProvider: ResourcesProvider,
) {

    operator fun invoke(
        rawCardNumber: String,
        rawCvv: String,
        rawExpDate: String,
        isCardholderNameRequired: Boolean,
        rawCardholderName: String,
    ): CardParsingResult {
        // validate card number
        val cardNumberValidationResult = cardNumberValidator.validate(rawCardNumber.trim())

        // validate cvv
        val cvvValidationResult = cvvValidator.validate(rawCvv.trim())

        // validate exp date
        var expDateValidationResult: ValidationResult
        val expDate = try {
            val expDate = CardExpDate.parse(rawExpDate)
            expDateValidationResult = expDateValidator.validate(expDate)
            expDate
        } catch (e: CardExpirationDateException) {
            expDateValidationResult =
                ValidationResult.Error(
                    resourcesProvider.getString(R.string.rozetka_pay_form_validation_exp_date_invalid)
                )
            null
        }

        // validate cardholder name
        val cardholderNameValidationResult = if (isCardholderNameRequired) {
            cardholderNameValidator.validate(rawCardholderName.trim())
        } else {
            ValidationResult.Valid
        }

        return if (cardNumberValidationResult.isValid
            && cvvValidationResult.isValid
            && expDateValidationResult.isValid
            && cardholderNameValidationResult.isValid
        ) {
            CardParsingResult.Success(
                CardData(
                    number = rawCardNumber.trim(),
                    cvv = rawCvv.trim(),
                    expDate = expDate!!,
                    cardholderName = rawCardholderName.trim()
                )
            )
        } else {
            CardParsingResult.Error(
                cardNumberError = (cardNumberValidationResult as? ValidationResult.Error)?.message,
                cvvError = (cvvValidationResult as? ValidationResult.Error)?.message,
                expDateError = (expDateValidationResult as? ValidationResult.Error)?.message,
                cardholderNameError = (cardholderNameValidationResult as? ValidationResult.Error)?.message
            )
        }
    }
}

internal sealed class CardParsingResult {
    data class Success(val cardData: CardData) : CardParsingResult()
    data class Error(
        val cardNumberError: String? = null,
        val cvvError: String? = null,
        val expDateError: String? = null,
        val cardholderNameError: String? = null,
    ) : CardParsingResult()
}