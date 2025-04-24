package billetera_seca.controller

import billetera_seca.exceptions.InvalidEmailFormatException
import billetera_seca.exceptions.UserAlreadyExistsException
import billetera_seca.exceptions.WeakPasswordException
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

}
