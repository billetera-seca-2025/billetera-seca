package billetera_seca.controller

import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.UserAlreadyExistsException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.exception.WeakPasswordException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(exception: UserAlreadyExistsException): ResponseEntity<String> {
        // Returns a 400 Bad Request response with the exception message
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidEmailFormatException::class)
    fun handleInvalidEmailFormat(exception: InvalidEmailFormatException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(WeakPasswordException::class)
    fun handleWeakPassword(exception: WeakPasswordException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(exception: UserNotFoundException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InsufficientBalanceException::class)
    fun handleInsufficientBalance(exception: InsufficientBalanceException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

}
