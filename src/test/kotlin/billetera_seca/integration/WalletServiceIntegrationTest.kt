package billetera_seca.integration

import billetera_seca.BaseTest
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.repository.UserRepository
import billetera_seca.service.user.UserService
import billetera_seca.service.wallet.WalletService
import billetera_seca.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import billetera_seca.model.dto.InstantDebitRequest
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
}