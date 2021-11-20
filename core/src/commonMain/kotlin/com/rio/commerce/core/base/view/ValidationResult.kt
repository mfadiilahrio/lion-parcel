package com.rio.commerce.core.base.view

sealed class ValidationResult<T> {
    val isValid: Boolean
        get() {
            return when (this) {
                ok<T>() -> true
                else -> false
            }
        }

    class Validating<T> : ValidationResult<T>()
    class Empty<T> : ValidationResult<T>()

    data class Ok<T>(val message: T?) : ValidationResult<T>()
    data class Failed<T>(val message: T) : ValidationResult<T>()

    companion object {
        fun <T> validating(): ValidationResult<T> =
            Validating()

        fun <T> empty(): ValidationResult<T> =
            Empty()

        fun <T> ok(): ValidationResult<T> =
            Ok(null)

        fun <T> ok(message: T): ValidationResult<T> =
            Ok(message)

        fun <T> failed(message: T): ValidationResult<T> =
            Failed(message)
    }
}

enum class TextInputValidation {
    INVALID_LENGTH,
    INVALID_CHARACTERS,
    DUPLICATION,
    NOT_MATCH
}

val String.alphabetAndSpace: Boolean get() = this.contains("^[a-zA-Z\\s]+$".toRegex())
val String.alphabetSpaceAndNumeric: Boolean get() = this.contains("^[a-zA-Z0-9\\s]+$".toRegex())
val String.validEmail: Boolean get() = this.contains("^[a-zA-Z0-9.!#\$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*\$".toRegex())
val String.validPassword: Boolean get() = this.contains("^(?=.*?[A-Z])(?=.*?[0-9]).{8,}\$".toRegex())
val String.validPhone: Boolean get() = this.contains("([+]?\\d{1,2}[.-\\\\s]?)?(\\d{3}[.-]?){2}\\d{3}".toRegex())