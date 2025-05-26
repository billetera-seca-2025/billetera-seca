package billetera_seca.util

import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.WeakPasswordException

object UserValidator {
    fun validateEmail(email: String) {
        if (!email.matches(Regex("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
            throw InvalidEmailFormatException("Invalid email format")
        }
    }

    fun validatePassword(password: String) {
        if (password.length < 6) {
            throw WeakPasswordException("Password too short")
        }
    }
}
