package billetera_seca.controller

import billetera_seca.model.dto.LoginRequest
import billetera_seca.model.dto.RegisterRequest
import billetera_seca.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginRequest): ResponseEntity<String> {
        // Aquí debes validar el email y password
        val user = userService.findByEmail(credentials.email)

        if (user != null && user.password == credentials.password) {
            return ResponseEntity.ok("Login successful")
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password")
    }

    @PostMapping("/register")
    fun register(@RequestBody credentials: RegisterRequest): ResponseEntity<String> {
        // Aquí debes crear un nuevo usuario en la base de datos
        val existingUser = userService.findByEmail(credentials.email)
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists")
        }

        userService.createUser(credentials.email, credentials.password)
        return ResponseEntity.ok("User created successfully")
    }
}
