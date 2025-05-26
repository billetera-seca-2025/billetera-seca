package billetera_seca.controller

import billetera_seca.model.dto.LoginRequest
import billetera_seca.model.dto.RegisterRequest
import billetera_seca.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginRequest): ResponseEntity<String> {
        val user = userService.findByEmail(credentials.email)

        if (user != null) {
            if (passwordMatches(credentials.password, user.password)) {
                return ResponseEntity.ok("Login successful")
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password")
    }

    private fun passwordMatches(plainPassword: String, hashedPassword: String): Boolean {
        val encoder = BCryptPasswordEncoder()
        return encoder.matches(plainPassword, hashedPassword)
    }

    @PostMapping("/register")
    fun register(@RequestBody credentials: RegisterRequest): ResponseEntity<String> {
        val existingUser = userService.findByEmail(credentials.email)
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists")
        }

        userService.createUser(credentials.email, credentials.password)
        return ResponseEntity.ok("User created successfully")
    }
}
