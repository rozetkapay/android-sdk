package com.rozetkapay.sdk.domain.validators

@Suppress("unused")
abstract class Validator<in T> {

    abstract fun validate(value: T): ValidationResult

    fun isValid(value: T): Boolean = validate(value).isValid
}

sealed class ValidationResult(
    val isValid: Boolean,
) {

    data object Valid : ValidationResult(true)

    data class Error(
        val message: String,
    ) : ValidationResult(false)
}

@Suppress("unused")
open class ValidatorsComposer<T>(validators: List<Validator<T>>? = null) : Validator<T>() {
    private val allValidators: MutableList<Validator<T>> = validators?.toMutableList() ?: ArrayList()

    fun addValidator(validator: Validator<T>) {
        allValidators.add(validator)
    }

    override fun validate(value: T): ValidationResult {
        if (allValidators.isNotEmpty()) {
            for (validator in allValidators) {
                val validationResult = validator.validate(value)
                if (validationResult is ValidationResult.Error) {
                    return validationResult
                }
            }
        }
        return ValidationResult.Valid
    }
}

fun <T> validatorOf(vararg validators: Validator<T>): Validator<T> {
    return ValidatorsComposer(validators.toList())
}