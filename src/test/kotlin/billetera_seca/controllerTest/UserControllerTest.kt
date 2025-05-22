package billetera_seca.controllerTest

import billetera_seca.controller.UserController
import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.model.dto.LoginRequest
import billetera_seca.model.dto.RegisterRequest
import billetera_seca.service.user.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

class UserControllerTest {

    private lateinit var userService: UserService
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder
    private lateinit var userController: UserController

    @BeforeEach
    fun setup() {
        userService = mock(UserService::class.java)
        bCryptPasswordEncoder = BCryptPasswordEncoder()
        userController = UserController(userService)
    }

    @Test
    fun `should return 200 and success message on successful login`() {
        val wallet = Wallet(id = UUID.randomUUID(), balance = 0.0)
        val user = User(
            id = UUID.randomUUID(),
            email = "test@example.com",
            password = bCryptPasswordEncoder.encode("password"),
            wallet = wallet
        )
        val loginRequest = LoginRequest(email = "test@example.com", password = "password")

        `when`(userService.findByEmail(loginRequest.email)).thenReturn(user)

        val response = userController.login(loginRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Login successful", response.body)
    }

    @Test
    fun `should return 401 on invalid login`() {
        val loginRequest = LoginRequest(email = "test@example.com", password = "wrongPassword")

        `when`(userService.findByEmail(loginRequest.email)).thenReturn(null)

        val response = userController.login(loginRequest)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Invalid email or password", response.body)
    }

    @Test
    fun `should return 201 on successful registration`() {
        val registerRequest = RegisterRequest(email = "newuser@example.com", password = "password")

        `when`(userService.findByEmail(registerRequest.email)).thenReturn(null)

        val response = userController.register(registerRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User created successfully", response.body)
        verify(userService).createUser(registerRequest.email, registerRequest.password)
    }

    @Test
    fun `should return 400 if user already exists during registration`() {
        val wallet = Wallet(id = UUID.randomUUID(), balance = 0.0)
        val existingUser = User(
            id = UUID.randomUUID(),
            email = "existinguser@example.com",
            password = bCryptPasswordEncoder.encode("password"),
            wallet = wallet
        )
        val registerRequest = RegisterRequest(email = "existinguser@example.com", password = "password")

        `when`(userService.findByEmail(registerRequest.email)).thenReturn(existingUser)

        val response = userController.register(registerRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("User already exists", response.body)
    }

    @Test
    fun `should return 200 on successful user update`() {
        val wallet = Wallet(id = UUID.randomUUID(), balance = 0.0)
        val existingUser = User(
            id = UUID.randomUUID(),
            email = "old@example.com",
            password = bCryptPasswordEncoder.encode("password"),
            wallet = wallet
        )
        val updatedRequest = RegisterRequest(email = "updated@example.com", password = "newPassword")

        // Stub findById to return the existing user
        `when`(userService.findById(existingUser.id)).thenReturn(existingUser)

        // Stub findByEmail to return null (email not in use by another user)
        `when`(userService.findByEmail(updatedRequest.email)).thenReturn(null)

        val response = userController.updateUser(existingUser.id, updatedRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User updated successfully", response.body)

    }
    @Test
    fun `should return 404 if user not found during update`() {
        val updatedRequest = RegisterRequest(email = "updated@example.com", password = "newPassword")
        val randomUUID = UUID.randomUUID()

        `when`(userService.findById(randomUUID)).thenReturn(null)

        val response = userController.updateUser(randomUUID, updatedRequest)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("User not found", response.body)
    }

    @Test
    fun `should return 200 on successful user deletion`() {
        val userId = UUID.randomUUID()

        doNothing().`when`(userService).deleteUser(userId)

        val response = userController.deleteUser(userId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("User deleted successfully", response.body)
        verify(userService).deleteUser(userId)
    }
}