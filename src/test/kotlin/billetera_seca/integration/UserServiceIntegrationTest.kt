package billetera_seca.integration

import billetera_seca.BaseTest
import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.UserAlreadyExistsException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.exception.WeakPasswordException
import billetera_seca.repository.UserRepository
import billetera_seca.service.user.UserService
import billetera_seca.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest: BaseTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `should persist user with wallet`() {
        // Arrange
        val testUser = TestUtils.createTestUser()

        // Act
        val savedUser = userService.createUser(testUser.email, testUser.password, testUser.wallet.balance)

        // Assert
        val userFromDb = userRepository.findById(savedUser.id).get()

        // Make sure the email and wallet are correctly persisted
        assertEquals(testUser.email, userFromDb.email)
        assertEquals(1000.0, userFromDb.wallet.balance)
    }

    @Test
    fun `should throw exception if email already exists`() {
        // Arrange
        val testUser1 = TestUtils.createTestUser()
        userService.createUser(testUser1.email, testUser1.password, testUser1.wallet.balance)

        // Act & Assert
        val exception = assertThrows<RuntimeException> {
            userService.createUser(testUser1.email, testUser1.password, testUser1.wallet.balance)
        }
        assertEquals("Email already registered", exception.message)
    }

    @Test
    fun `should create user without initial balance`() {
        // Arrange
        val testUser = TestUtils.createTestUser()
        testUser.wallet.balance = 0.0

        // Act
        val savedUser = userService.createUser(testUser.email, testUser.password, testUser.wallet.balance)

        // Assert
        val userFromDb = userRepository.findById(savedUser.id).get()
        assertEquals(testUser.email, userFromDb.email)
        assertEquals(0.0, userFromDb.wallet.balance)
    }

    @Test
    fun `should create multiple users correctly`() {
        // Arrange
        val testUser1 = TestUtils.createTestUser()
        val testUser2 = TestUtils.createTestUser(email = "user2@example.com")

        // Act
        val savedUser1 = userService.createUser(testUser1.email, testUser1.password, testUser1.wallet.balance)
        val savedUser2 = userService.createUser(testUser2.email, testUser2.password, testUser2.wallet.balance)

        // Assert
        val userFromDb1 = userRepository.findById(savedUser1.id).get()
        val userFromDb2 = userRepository.findById(savedUser2.id).get()
        assertEquals(testUser1.email, userFromDb1.email)
        assertEquals(testUser2.email, userFromDb2.email)
        assertNotEquals(userFromDb1.id, userFromDb2.id) // Ensure users have different IDs
    }

    @Test
    fun `should throw exception for invalid email`() {
        // Arrange
        val invalidEmail = "invalid-email"
        val password = "ValidPassword123"

        // Act & Assert
        val exception = assertThrows<InvalidEmailFormatException> {
            userService.createUser(invalidEmail, password)
        }
        assertEquals("Invalid email format", exception.message)
    }

    @Test
    fun `should throw exception for weak password`() {
        // Arrange
        val email = "user@example.com"
        val weakPassword = "123"

        // Act & Assert
        val exception = assertThrows<WeakPasswordException> {
            userService.createUser(email, weakPassword)
        }
        assertEquals("Password too short", exception.message)
    }
    @Test
    fun `should delete user successfully`() {
        // Arrange
        val testUser = TestUtils.createTestUser()
        val savedUser = userService.createUser(testUser.email, testUser.password, testUser.wallet.balance)

        // Act
        userService.deleteUser(savedUser.id)

        // Assert
        val userExists = userRepository.findById(savedUser.id).isPresent
        assertEquals(false, userExists)
    }

    @Test
    fun `should throw exception when deleting non-existent user`() {
        // Arrange
        val nonExistentUserId = UUID.randomUUID()

        // Act & Assert
        val exception = assertThrows<UserNotFoundException> {
            userService.deleteUser(nonExistentUserId)
        }
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `should find user by email`() {
        // Arrange
        val testUser = TestUtils.createTestUser()
        userService.createUser(testUser.email, testUser.password, testUser.wallet.balance)

        // Act
        val foundUser = userService.findByEmail(testUser.email)

        // Assert
        assertEquals(testUser.email, foundUser?.email)
    }

    @Test
    fun `should throw exception when updating non-existent user`() {
        // Arrange
        val nonExistentUser = TestUtils.createTestUser()
        nonExistentUser.id = UUID.randomUUID()

        // Act & Assert
        val exception = assertThrows<UserNotFoundException> {
            userService.updateUser(nonExistentUser)
        }
        assertEquals("User not found", exception.message)
    }



}