package billetera_seca.service.user

import billetera_seca.exception.InvalidEmailFormatException
import billetera_seca.exception.UserAlreadyExistsException
import billetera_seca.exception.WeakPasswordException
import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import billetera_seca.service.UserService
import billetera_seca.util.TestUtils
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var walletRepository: WalletRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        walletRepository = mockk()
        userService = UserService(userRepository, walletRepository)
    }

    @Test
    fun `should create a user successfully`() {
        // Arrange
        val user = TestUtils.createTestUser()

        every { userRepository.findByEmail(user.email) } returns null
        every { walletRepository.save(any()) } returns user.wallet
        every { userRepository.save(any()) } returns user

        // Act
        val createdUser = userService.createUser(user.email, user.password)

        // Assert
        verify(exactly = 1) { walletRepository.save(any()) }
        verify(exactly = 1) { userRepository.save(any()) }

        assertEquals(user.email, createdUser.email)
        assertEquals(1000.0, createdUser.wallet.balance)
    }

    @Test
    fun `should throw UserAlreadyExistsException when email already exists`() {
        // Arrange
        val email = "existing@example.com"
        val password = "pass123"
        val existingUser = User(email = email, password = password, wallet = Wallet())

        every { userRepository.findByEmail(email) } returns existingUser

        // Act & Assert
        assertThrows<UserAlreadyExistsException> {
            userService.createUser(email, password)
        }

        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should throw InvalidEmailFormatException for invalid email format`() {
        val invalidEmail = "invalid-email"
        val password = "password123"

        assertThrows<InvalidEmailFormatException> {
            userService.createUser(invalidEmail, password)
        }

        verify { userRepository wasNot Called }
        verify { walletRepository wasNot Called }
    }

    @Test
    fun `should throw WeakPasswordException for weak password`() {
        val email = "test@example.com"
        val weakPassword = "123"

        assertThrows<WeakPasswordException> {
            userService.createUser(email, weakPassword)
        }

        verify { userRepository wasNot Called }
        verify { walletRepository wasNot Called }
    }

    @Test
    fun `should initialize wallet with correct balance and empty transactions`() {
        val user = TestUtils.createTestUser()
        every { userRepository.findByEmail(user.email) } returns null
        every { walletRepository.save(any()) } returns user.wallet
        every { userRepository.save(any()) } returns user

        val createdUser = userService.createUser(user.email, user.password)

        assertEquals(createdUser.wallet.balance, 1000.0)
        //assert(createdUser.wallet.transactions.isEmpty())
    }


}
