package billetera_seca.util

import billetera_seca.exception.EmptyPasswordException
import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.WeakPasswordException

object UserValidator {
    fun validateEmail(email: String?) {
        if (email.isNullOrEmpty() || !email.contains("@")) {
            throw InvalidEmailFormatException("Invalid email format")
        }
    }

    fun validatePassword(password: String?) {
        if (password == null) {
            throw EmptyPasswordException("Password cannot be null")
        }
        if (password.isEmpty()) {
            throw EmptyPasswordException("Password cannot be empty")
        }
        if (password.length < 6) {
            throw WeakPasswordException("Password too short")
        }
    }
}
