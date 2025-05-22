package billetera_seca.service.user

import billetera_seca.exception.*
import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import billetera_seca.util.TestUtils
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

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
        val user = TestUtils.createTestUser()

        every { userRepository.findByEmail(user.email) } returns null
        every { walletRepository.save(any()) } returns user.wallet
        every { userRepository.save(any()) } returns user

        val createdUser = userService.createUser(user.email, user.password)

        verify(exactly = 1) { walletRepository.save(any()) }
        verify(exactly = 1) { userRepository.save(any()) }

        assertEquals(user.email, createdUser.email)
        assertEquals(1000.0, createdUser.wallet.balance)
    }

    @Test
    fun `should throw UserAlreadyExistsException when email already exists`() {
        val email = "existing@example.com"
        val password = "pass123"
        val existingUser = User(email = email, password = password, wallet = Wallet())

        every { userRepository.findByEmail(email) } returns existingUser

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
    fun `should throw InvalidEmailFormatException when email is null`() {
        val email: String = null.toString()
        val password = "password123"

        assertThrows<InvalidEmailFormatException> {
            userService.createUser(email, password)
        }

        verify { userRepository wasNot Called }
        verify { walletRepository wasNot Called }
    }
    @Test
    fun `should throw InvalidEmailFormatException when email is empty`() {
        val email = ""
        val password = "password123"

        assertThrows<InvalidEmailFormatException> {
            userService.createUser(email, password)
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
    fun `should throw EmptyPasswordException when password is empty`() {
        val email = "test@example.com"
        val password = ""

        assertThrows<EmptyPasswordException> {
            userService.createUser(email, password)
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
        assert(createdUser.wallet.transactions.isEmpty())
    }

    @Test
    fun `should throw UserNotFoundException when updating a non-existent user`() {
        val user = TestUtils.createTestUser()

        every { userRepository.findById(user.id) } returns Optional.empty()

        assertThrows<UserNotFoundException> {
            userService.updateUser(user)
        }

        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should throw UserNotFoundException when deleting a non-existent user`() {
        val userId = UUID.randomUUID()

        every { userRepository.findById(userId) } returns Optional.empty()

        assertThrows<UserNotFoundException> {
            userService.deleteUser(userId)
        }

        verify(exactly = 0) { userRepository.delete(any()) }
    }

    @Test
    fun `should find user by email`() {
        val user = TestUtils.createTestUser()

        every { userRepository.findByEmail(user.email) } returns user

        val foundUser = userService.findByEmail(user.email)

        assertEquals(user, foundUser)
        verify(exactly = 1) { userRepository.findByEmail(user.email) }
    }

    @Test
    fun `should find user by id`() {
        val user = TestUtils.createTestUser()

        every { userRepository.findById(user.id) } returns Optional.of(user)

        val foundUser = userService.findById(user.id)

        assertEquals(user, foundUser)
        verify(exactly = 1) { userRepository.findById(user.id) }
    }

    @Test
    fun `should return null when user is not found by email`() {
        val email = "nonexistent@example.com"

        every { userRepository.findByEmail(email) } returns null

        val foundUser = userService.findByEmail(email)

        assertEquals(null, foundUser)
        verify(exactly = 1) { userRepository.findByEmail(email) }
    }

    @Test
    fun `should return null when user is not found by id`() {
        val userId = UUID.randomUUID()

        every { userRepository.findById(userId) } returns Optional.empty()

        val foundUser = userService.findById(userId)

        assertEquals(null, foundUser)
        verify(exactly = 1) { userRepository.findById(userId) }
    }
}