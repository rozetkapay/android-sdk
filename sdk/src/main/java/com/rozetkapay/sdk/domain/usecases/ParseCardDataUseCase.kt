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
    private val emailValidator: Validator<String>,
    private val cardNameValidator: Validator<String>,
    private val resourcesProvider: ResourcesProvider,
) {

    operator fun invoke(params: Parameters): CardParsingResult {
        // validate card number
        val cardNumber = params.rawCardNumber.trim()
        val cardNumberValidationResult = cardNumberValidator.validate(cardNumber)

        // validate cvv
        val cvv = params.rawCvv.trim()
        val cvvValidationResult = cvvValidator.validate(cvv)

        // validate exp date
        var expDateValidationResult: ValidationResult
        val expDate = try {
            val expDate = CardExpDate.parse(params.rawExpDate)
            expDateValidationResult = expDateValidator.validate(expDate)
            expDate
        } catch (e: CardExpirationDateException) {
            expDateValidationResult = ValidationResult.Error(
                message = resourcesProvider.getString(R.string.rozetka_pay_form_validation_exp_date_invalid)
            )
            null
        }

        // validate cardholder name
        val cardholderName = params.rawCardholderName.trim()
        val cardholderNameValidationResult = if (params.isCardholderNameRequired) {
            cardholderNameValidator.validate(cardholderName)
        } else {
            ValidationResult.Valid
        }

        // validate cardholder email
        val cardholderEmail = params.rawCardholderEmail.trim()
        val cardholderEmailValidationResult =
            if (params.isCardholderEmailRequired || params.rawCardholderEmail.isNotBlank()) {
                emailValidator.validate(cardholderEmail)
            } else {
                ValidationResult.Valid
            }

        // validate card name
        val cardName = params.rawCardName.trim()
        val cardNameValidationResult = if (params.isCardNameRequired || params.rawCardName.isNotBlank()) {
            cardNameValidator.validate(cardName)
        } else {
            ValidationResult.Valid
        }

        return if (cardNumberValidationResult.isValid
            && cvvValidationResult.isValid
            && expDateValidationResult.isValid
            && cardholderNameValidationResult.isValid
            && cardholderEmailValidationResult.isValid
        ) {
            CardParsingResult.Success(
                CardData(
                    number = cardNumber,
                    cvv = cvv,
                    expDate = expDate!!,
                    cardholderName = cardholderName,
                    cardholderEmail = cardholderEmail,
                    cardName = cardName,
                )
            )
        } else {
            CardParsingResult.Error(
                cardNumberError = (cardNumberValidationResult as? ValidationResult.Error)?.message,
                cvvError = (cvvValidationResult as? ValidationResult.Error)?.message,
                expDateError = (expDateValidationResult as? ValidationResult.Error)?.message,
                cardholderNameError = (cardholderNameValidationResult as? ValidationResult.Error)?.message,
                cardholderEmailError = (cardholderEmailValidationResult as? ValidationResult.Error)?.message,
                cardNameError = (cardNameValidationResult as? ValidationResult.Error)?.message,
            )
        }
    }

    internal data class Parameters(
        val rawCardNumber: String,
        val rawCvv: String,
        val rawExpDate: String,
        val rawCardholderName: String,
        val rawCardholderEmail: String,
        val rawCardName: String,
        val isCardholderNameRequired: Boolean,
        val isCardholderEmailRequired: Boolean,
        val isCardNameRequired: Boolean,
    )
}

internal sealed class CardParsingResult {

    data class Success(val cardData: CardData) : CardParsingResult()

    data class Error(
        val cardNumberError: String? = null,
        val cvvError: String? = null,
        val expDateError: String? = null,
        val cardholderNameError: String? = null,
        val cardholderEmailError: String? = null,
        val cardNameError: String? = null,
    ) : CardParsingResult()
}