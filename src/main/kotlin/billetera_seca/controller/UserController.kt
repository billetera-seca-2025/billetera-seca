package billetera_seca.controller

import billetera_seca.service.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping("/create")
    fun createUser(@RequestParam email: String, @RequestParam password: String): ResponseEntity<String> {
        return try {
            val user = userService.createUser(email, password)
            ResponseEntity.ok("User created successfully")
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
