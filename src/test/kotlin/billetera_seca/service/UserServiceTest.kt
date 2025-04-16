package billetera_seca.service

import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import billetera_seca.util.TestUtils
import io.mockk.*
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

        assert(createdUser.email == user.email)
        assert(createdUser.wallet.balance == 1000.0)
    }

    @Test
    fun `should throw exception when email already exists`() {
        // Arrange
        val email = "existing@example.com"
        val password = "pass"
        val existingUser = User(email = email, password = password, wallet = Wallet())

        every { userRepository.findByEmail(email) } returns existingUser

        // Act + Assert
        assertThrows<IllegalArgumentException> {
            userService.createUser(email, password)
        }

        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }
}
