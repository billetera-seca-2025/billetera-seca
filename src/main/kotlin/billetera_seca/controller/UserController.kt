package billetera_seca.controller

import billetera_seca.model.User
import billetera_seca.model.dto.LoginRequest
import billetera_seca.model.dto.RegisterRequest
import billetera_seca.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*


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

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody updatedUser: RegisterRequest
    ): ResponseEntity<String> {
        val existingUser = userService.findById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")

        val userWithSameEmail = userService.findByEmail(updatedUser.email)
        if (userWithSameEmail != null && userWithSameEmail.id != id) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use by another user")
        }

        val updated = existingUser.copy(
            email = updatedUser.email,
            password = BCryptPasswordEncoder().encode(updatedUser.password)
        )

        userService.updateUser(updated)
        return ResponseEntity.ok("User updated successfully")
    }
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<String> {
        userService.deleteUser(id)
        return ResponseEntity.ok("User deleted successfully")
    }

}
