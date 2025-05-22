package billetera_seca.util

import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.WeakPasswordException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UserValidatorTest {

    @Test
    fun `validateEmail should not throw exception for valid email`() {
        // Arrange
        val validEmail = "test@example.com"

        // Act & Assert - No exception should be thrown
        UserValidator.validateEmail(validEmail)
    }

    @ParameterizedTest
    @ValueSource(strings = ["test@example", "test@.com", "test@com", "test", "@example.com", "test@example..com"])
    fun `validateEmail should throw InvalidEmailFormatException for invalid email`(invalidEmail: String) {
        // Act & Assert
        assertThrows<InvalidEmailFormatException> {
            UserValidator.validateEmail(invalidEmail)
        }
    }

    @Test
    fun `validatePassword should not throw exception for valid password`() {
        // Arrange
        val validPassword = "password123"

        // Act & Assert - No exception should be thrown
        UserValidator.validatePassword(validPassword)
    }

    @Test
    fun `validatePassword should throw WeakPasswordException for short password`() {
        // Arrange
        val shortPassword = "12345" // Less than 6 characters

        // Act & Assert
        assertThrows<WeakPasswordException> {
            UserValidator.validatePassword(shortPassword)
        }
    }

    @Test
    fun `validatePassword should accept password with exactly 6 characters`() {
        // Arrange
        val borderlinePassword = "123456" // Exactly 6 characters

        // Act & Assert - No exception should be thrown
        UserValidator.validatePassword(borderlinePassword)
    }
}