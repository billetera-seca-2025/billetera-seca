package billetera_seca.controller

import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.UserAlreadyExistsException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.exception.WeakPasswordException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        exceptionHandler = GlobalExceptionHandler()
    }

    @Test
    fun `handleUserAlreadyExists should return BAD_REQUEST with exception message`() {
        // Arrange
        val exceptionMessage = "User already exists"
        val exception = UserAlreadyExistsException(exceptionMessage)

        // Act
        val response = exceptionHandler.handleUserAlreadyExists(exception)

        // Assert
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body == exceptionMessage)
    }

    @Test
    fun `handleInvalidEmailFormat should return BAD_REQUEST with exception message`() {
        // Arrange
        val exceptionMessage = "Invalid email format"
        val exception = InvalidEmailFormatException(exceptionMessage)

        // Act
        val response = exceptionHandler.handleInvalidEmailFormat(exception)

        // Assert
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body == exceptionMessage)
    }

    @Test
    fun `handleWeakPassword should return BAD_REQUEST with exception message`() {
        // Arrange
        val exceptionMessage = "Password is too weak"
        val exception = WeakPasswordException(exceptionMessage)

        // Act
        val response = exceptionHandler.handleWeakPassword(exception)

        // Assert
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body == exceptionMessage)
    }

    @Test
    fun `handleUserNotFound should return NOT_FOUND with exception message`() {
        // Arrange
        val exceptionMessage = "User not found"
        val exception = UserNotFoundException(exceptionMessage)

        // Act
        val response = exceptionHandler.handleUserNotFound(exception)

        // Assert
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body == exceptionMessage)
    }

    @Test
    fun `handleInsufficientBalance should return BAD_REQUEST with exception message`() {
        // Arrange
        val exceptionMessage = "Insufficient balance"
        val exception = InsufficientBalanceException(exceptionMessage)

        // Act
        val response = exceptionHandler.handleInsufficientBalance(exception)

        // Assert
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body == exceptionMessage)
    }
}