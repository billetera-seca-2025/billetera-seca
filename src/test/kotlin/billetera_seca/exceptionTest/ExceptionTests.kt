package billetera_seca.exceptionTest

import billetera_seca.controller.GlobalExceptionHandler
import billetera_seca.exception.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ExceptionTests {

    @Test
    fun `should create UserAlreadyExistsException with correct message`() {
        val exception = UserAlreadyExistsException("User already exists")
        assertEquals("User already exists", exception.message)
    }

    @Test
    fun `should create InvalidEmailFormatException with correct message`() {
        val exception = InvalidEmailFormatException("Invalid email format")
        assertEquals("Invalid email format", exception.message)
    }

    @Test
    fun `should create WeakPasswordException with correct message`() {
        val exception = WeakPasswordException("Password is too weak")
        assertEquals("Password is too weak", exception.message)
    }

    @Test
    fun `should create UserNotFoundException with correct message`() {
        val exception = UserNotFoundException("User not found")
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `should create InsufficientBalanceException with correct message`() {
        val exception = InsufficientBalanceException("Insufficient balance")
        assertEquals("Insufficient balance", exception.message)
    }

    @Test
    fun `should create SelfTransferException with correct message`() {
        val exception = SelfTransferException("Cannot transfer to self")
        assertEquals("Cannot transfer to self", exception.message)
    }

    @Test
    fun `should create NegativeOrZeroAmountException with correct message`() {
        val exception = NegativeOrZeroAmountException("Amount must be greater than zero")
        assertEquals("Amount must be greater than zero", exception.message)
    }

    @Test
    fun `should handle UserAlreadyExistsException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = UserAlreadyExistsException("User already exists")
        val response: ResponseEntity<String> = handler.handleUserAlreadyExists(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("User already exists", response.body)
    }

    @Test
    fun `should handle InvalidEmailFormatException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = InvalidEmailFormatException("Invalid email format")
        val response: ResponseEntity<String> = handler.handleInvalidEmailFormat(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid email format", response.body)
    }

    @Test
    fun `should handle WeakPasswordException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = WeakPasswordException("Password is too weak")
        val response: ResponseEntity<String> = handler.handleWeakPassword(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Password is too weak", response.body)
    }

    @Test
    fun `should handle UserNotFoundException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = UserNotFoundException("User not found")
        val response: ResponseEntity<String> = handler.handleUserNotFound(exception)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("User not found", response.body)
    }

    @Test
    fun `should handle InsufficientBalanceException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = InsufficientBalanceException("Insufficient balance")
        val response: ResponseEntity<String> = handler.handleInsufficientBalance(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Insufficient balance", response.body)
    }

    @Test
    fun `should handle SelfTransferException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = SelfTransferException("Cannot transfer to self")
        val response: ResponseEntity<String> = handler.handleSelfTransfer(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Cannot transfer to self", response.body)
    }

    @Test
    fun `should handle NegativeOrZeroAmountException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = NegativeOrZeroAmountException("Amount must be greater than zero")
        val response: ResponseEntity<String> = handler.handleNegativeOrZeroAmount(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Amount must be greater than zero", response.body)
    }

    @Test
    fun `should handle EmptyPasswordException and return correct response`() {
        val handler = GlobalExceptionHandler()
        val exception = EmptyPasswordException("Password cannot be empty")
        val response: ResponseEntity<String> = handler.handleEmptyPassword(exception)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Password cannot be empty", response.body)
    }
}