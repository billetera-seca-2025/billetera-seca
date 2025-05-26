package billetera_seca.controller

import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.model.dto.LoginRequest
import billetera_seca.model.dto.RegisterRequest
import billetera_seca.service.user.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserControllerTest {

    private lateinit var userService: UserService
    private lateinit var userController: UserController
    private lateinit var passwordEncoder: BCryptPasswordEncoder

    @BeforeEach
    fun setUp() {
        userService = mockk()
        passwordEncoder = BCryptPasswordEncoder()
        userController = UserController(userService)
    }

    @Test
    fun `login should return OK when credentials are valid`() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val hashedPassword = passwordEncoder.encode(password)
        val user = User(email = email, password = hashedPassword, wallet = Wallet())

        every { userService.findByEmail(email) } returns user

        // Act
        val response = userController.login(LoginRequest(email, password))

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == "Login successful")
        verify(exactly = 1) { userService.findByEmail(email) }
    }

    @Test
    fun `login should return UNAUTHORIZED when user not found`() {
        // Arrange
        val email = "nonexistent@example.com"
        val password = "password123"

        every { userService.findByEmail(email) } returns null

        // Act
        val response = userController.login(LoginRequest(email, password))

        // Assert
        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
        assert(response.body == "Invalid email or password")
        verify(exactly = 1) { userService.findByEmail(email) }
    }

    @Test
    fun `login should return UNAUTHORIZED when password is incorrect`() {
        // Arrange
        val email = "test@example.com"
        val correctPassword = "password123"
        val incorrectPassword = "wrongpassword"
        val hashedPassword = passwordEncoder.encode(correctPassword)
        val user = User(email = email, password = hashedPassword, wallet = Wallet())

        every { userService.findByEmail(email) } returns user

        // Act
        val response = userController.login(LoginRequest(email, incorrectPassword))

        // Assert
        assert(response.statusCode == HttpStatus.UNAUTHORIZED)
        assert(response.body == "Invalid email or password")
        verify(exactly = 1) { userService.findByEmail(email) }
    }

    @Test
    fun `register should return OK when user is created successfully`() {
        // Arrange
        val email = "newuser@example.com"
        val password = "password123"

        every { userService.findByEmail(email) } returns null
        every { userService.createUser(email, password) } returns User(email = email, password = password, wallet = Wallet())

        // Act
        val response = userController.register(RegisterRequest(email, password))

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == "User created successfully")
        verify(exactly = 1) { userService.findByEmail(email) }
        verify(exactly = 1) { userService.createUser(email, password) }
    }

    @Test
    fun `register should return BAD_REQUEST when user already exists`() {
        // Arrange
        val email = "existing@example.com"
        val password = "password123"
        val existingUser = User(email = email, password = password, wallet = Wallet())

        every { userService.findByEmail(email) } returns existingUser

        // Act
        val response = userController.register(RegisterRequest(email, password))

        // Assert
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body == "User already exists")
        verify(exactly = 1) { userService.findByEmail(email) }
        verify(exactly = 0) { userService.createUser(any(), any()) }
    }
}