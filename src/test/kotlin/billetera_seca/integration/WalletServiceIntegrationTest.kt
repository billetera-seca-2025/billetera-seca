package billetera_seca.integration

import billetera_seca.BaseTest
import billetera_seca.repository.UserRepository
import billetera_seca.service.user.UserService
import billetera_seca.service.wallet.WalletService
import billetera_seca.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import billetera_seca.dto.InstantDebitRequest
import billetera_seca.exception.*
import org.junit.jupiter.api.Tag


@SpringBootTest
@ActiveProfiles("test")
class WalletServiceIntegrationTest: BaseTest() {

    @Autowired
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `should get balance`() {
        // Arrange
        val testUser = TestUtils.createTestUser()
        val savedUser = userService.createUser(testUser.email, testUser.password, testUser.wallet.balance)

        // Act
        val balance = walletService.getBalance(savedUser.email)

        // Assert
        assertEquals(1000.0, balance)
    }

    @Test
    fun `should transfer money between wallets`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val receiver = TestUtils.createTestUser("email2@gmail.com")

        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)

        val amountToTransfer = 100.0
        // Act
        walletService.transfer(savedSender.email, savedReceiver.email, amountToTransfer)
        // Assert
        val updatedSender = userRepository.findById(savedSender.id).get()
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(900.0, updatedSender.wallet.balance)
        assertEquals(1100.0, updatedReceiver.wallet.balance)
    }

    @Test
    fun `should throw InsufficientBalanceException when transferring more than balance`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val receiver = TestUtils.createTestUser("email2@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val amountToTransfer = 2000.0
        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<InsufficientBalanceException> {
            walletService.transfer(savedSender.email, savedReceiver.email, amountToTransfer)
        }
        assertEquals("Insufficient funds", exception.message)
    }

    @Tag("integration")
    @Test
    fun `should throw failed transfer when debit is rejected`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val receiver = TestUtils.createTestUser("email2@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val amountToTransfer = 100.0
        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                savedSender.email,
                savedReceiver.email,
                amountToTransfer
            )
        )
        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `should throw UserNotFoundException when getting balance of non-existent user`() {
        // Arrange
        val nonExistentEmail = "nonexistent@example.com"

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<UserNotFoundException> {
            walletService.getBalance(nonExistentEmail)
        }
        assertEquals(nonExistentEmail, exception.message)
    }

    fun handleInstantDebitRequest(instantDebitRequest: InstantDebitRequest): Boolean {
        userService.findByEmail(instantDebitRequest.payerEmail) ?: throw UserNotFoundException(instantDebitRequest.payerEmail)
        userService.findByEmail(instantDebitRequest.collectorEmail) ?: throw UserNotFoundException(instantDebitRequest.collectorEmail)

        // Simulate instant debit authorization logic
        val instantDebitApproved = simulateInstantDebitAuthorization(instantDebitRequest.amount)
        if (instantDebitApproved) {
            // If the Instant Debit is approved, proceed with the transfer
            walletService.handleInstantDebitRequest(
                InstantDebitRequest(
                    instantDebitRequest.payerEmail,
                    instantDebitRequest.collectorEmail,
                    instantDebitRequest.amount
                )
            )
            return true
        }
        return false
    }

    private fun simulateInstantDebitAuthorization(amount: Double): Boolean {
        // Simulate approval logic (e.g., approve if amount <= 1000.0)
        return amount <= 1000.0
    }

    @Test
    fun `should throw exception when transferring zero or negative amount`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val receiver = TestUtils.createTestUser("email2@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)

        // Act & Assert
        val zeroAmountException = org.junit.jupiter.api.assertThrows<NegativeOrZeroAmountException> {
            walletService.transfer(savedSender.email, savedReceiver.email, 0.0)
        }
        assertEquals("Transfer amount must be greater than zero", zeroAmountException.message)

        val negativeAmountException = org.junit.jupiter.api.assertThrows<NegativeOrZeroAmountException> {
            walletService.transfer(savedSender.email, savedReceiver.email, -100.0)
        }
        assertEquals("Transfer amount must be greater than zero", negativeAmountException.message)
    }
    @Test
    fun `should throw exception when transferring money to self`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val amountToTransfer = 100.0

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<SelfTransferException> {
            walletService.transfer(savedSender.email, savedSender.email, amountToTransfer)
        }
        assertEquals("Cannot transfer money to the same account", exception.message)
    }

    @Test
    fun `should throw UserNotFoundException when receiver does not exist`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val nonExistentReceiverEmail = "nonexistent@example.com"
        val amountToTransfer = 100.0

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<UserNotFoundException> {
            walletService.transfer(savedSender.email, nonExistentReceiverEmail, amountToTransfer)
        }
        assertEquals(nonExistentReceiverEmail, exception.message)
    }

    @Test
    fun `should throw UserNotFoundException when sender does not exist`() {
        // Arrange
        val receiver = TestUtils.createTestUser("email2@gmail.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val nonExistentSenderEmail = "nonexistent@example.com"
        val amountToTransfer = 100.0

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<UserNotFoundException> {
            walletService.transfer(nonExistentSenderEmail, savedReceiver.email, amountToTransfer)
        }
        assertEquals(nonExistentSenderEmail, exception.message)
    }

    @Test
    fun `should not change balances if transfer fails`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val receiver = TestUtils.createTestUser("email2@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val amountToTransfer = 2000.0 // More than sender's balance

        // Act
        try {
            walletService.transfer(savedSender.email, savedReceiver.email, amountToTransfer)
        } catch (e: InsufficientBalanceException) {
            // Expected exception
        }

        // Assert
        val updatedSender = userRepository.findById(savedSender.id).get()
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedSender.wallet.balance) // Sender's balance should remain unchanged
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Receiver's balance should remain unchanged
    }

    @Test
    fun `should register transactions after successful transfer`() {
        // Arrange
        val sender = TestUtils.createTestUser("email@gmail.com")
        val receiver = TestUtils.createTestUser("email2@gmail.com")
        val savedSender = userService.createUser(sender.email, sender.password, sender.wallet.balance)
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val amountToTransfer = 100.0

        // Act
        walletService.transfer(savedSender.email, savedReceiver.email, amountToTransfer)

        // Assert
        // Assuming transactionService has a method to fetch transactions
        val senderTransactions = walletService.getBalance(savedSender.email)
        val receiverTransactions = walletService.getBalance(savedReceiver.email)

        // Check if the transactions are registered correctly
        assertEquals(900.0, senderTransactions)
        assertEquals(1100.0, receiverTransactions)

    }
}