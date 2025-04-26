package billetera_seca.service.wallet

import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.model.Wallet
import billetera_seca.repository.WalletRepository
import billetera_seca.service.movement.MovementService
import billetera_seca.service.user.UserService
import billetera_seca.util.TestUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.*
import org.junit.jupiter.api.assertThrows

class WalletServiceTest {

    private lateinit var walletRepository: WalletRepository
    private lateinit var movementService: MovementService
    private lateinit var userService: UserService
    private lateinit var walletService: WalletService

    @BeforeEach
    fun setUp() {
        walletRepository = mockk()
        movementService = mockk()
        userService = mockk()
        walletService = WalletService(walletRepository, movementService, userService)
    }

    @Test
    fun `getBalance should return correct wallet balance`() {
        // Arrange
        val email = "user@example.com"
        val user = TestUtils.createTestUser(email)

        every { userService.findByEmail(email) } returns user

        // Act
        val balance = walletService.getBalance(email)

        // Assert
        assertEquals(1000.0, balance)
        verify(exactly = 1) { userService.findByEmail(email) }
    }

    @Test
    fun `transfer should successfully transfer funds between users`() {
        // Arrange
        val senderEmail = "sender@example.com"
        val receiverEmail = "receiver@example.com"
        val amount = 100.0

        val sender = TestUtils.createTestUser(senderEmail).apply {
            wallet.balance = 200.0
        }

        val receiver = TestUtils.createTestUser(receiverEmail).apply {
            wallet.balance = 50.0
        }

        every { userService.findByEmail(senderEmail) } returns sender
        every { userService.findByEmail(receiverEmail) } returns receiver
        every { walletRepository.save(any()) } answers { firstArg<Wallet>() }
        every { movementService.registerOutcome(sender.wallet, amount) } just Runs
        every { movementService.registerIncome(receiver.wallet, amount) } just Runs

        // Act
        walletService.transfer(senderEmail, receiverEmail, amount)

        // Assert
        assertEquals(100.0, sender.wallet.balance)  // 200 - 100
        assertEquals(150.0, receiver.wallet.balance)  // 50 + 100

        verify(exactly = 1) { walletRepository.save(sender.wallet) }
        verify(exactly = 1) { walletRepository.save(receiver.wallet) }
        verify(exactly = 1) { movementService.registerOutcome(sender.wallet, amount) }
        verify(exactly = 1) { movementService.registerIncome(receiver.wallet, amount) }
    }

    @Test
    fun `transfer should throw UserNotFoundException when sender not found`() {
        // Arrange
        val senderEmail = "nonexistentSender@example.com"
        val receiverEmail = "receiver@example.com"
        val amount = 100.0

        every { userService.findByEmail(senderEmail) } returns null

        // Act & Assert
        assertThrows<UserNotFoundException> {
            walletService.transfer(senderEmail, receiverEmail, amount)
        }

        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { movementService.registerOutcome(any(), any()) }
    }

    @Test
    fun `transfer should throw UserNotFoundException when receiver not found`() {
        // Arrange
        val senderEmail = "sender@example.com"
        val receiverEmail = "nonexistentReceiver@example.com"
        val amount = 100.0

        val sender = TestUtils.createTestUser(senderEmail).apply {
            wallet.balance = 200.0
        }

        every { userService.findByEmail(senderEmail) } returns sender
        every { userService.findByEmail(receiverEmail) } returns null

        // Act & Assert
        assertThrows<UserNotFoundException> {
            walletService.transfer(senderEmail, receiverEmail, amount)
        }

        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { movementService.registerOutcome(any(), any()) }
    }

    @Test
    fun `transfer should throw InsufficientBalanceException when sender has insufficient balance`() {
        // Arrange
        val senderEmail = "sender@example.com"
        val receiverEmail = "receiver@example.com"
        val amount = 500.0  // More than the sender's balance

        val sender = TestUtils.createTestUser(senderEmail).apply {
            wallet.balance = 200.0  // Insufficient balance
        }

        val receiver = TestUtils.createTestUser(receiverEmail).apply {
            wallet.balance = 50.0
        }

        every { userService.findByEmail(senderEmail) } returns sender
        every { userService.findByEmail(receiverEmail) } returns receiver

        // Act & Assert
        assertThrows<InsufficientBalanceException> {
            walletService.transfer(senderEmail, receiverEmail, amount)
        }

        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { movementService.registerOutcome(any(), any()) }
    }

}