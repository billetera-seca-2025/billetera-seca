package billetera_seca.integration

import billetera_seca.BaseTest
import billetera_seca.config.TestWebClientConfig
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.model.dto.InstantDebitRequest
import billetera_seca.repository.UserRepository
import billetera_seca.service.user.UserService
import billetera_seca.service.wallet.WalletService
import billetera_seca.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(TestWebClientConfig::class)
class WalletServiceIntegrationTest: BaseTest() {

    @Autowired
    private lateinit var walletService: WalletService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        // Clean up any test data if needed
        userRepository.deleteAll()
    }

    @Test
    fun `should reject instant debit request when bank does not exist`() {
        // Arrange
        val receiver = TestUtils.createTestUser("receiver@example.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val invalidBankName = "InvalidBank"
        val amount = 100.0

        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                receiverEmail = savedReceiver.email,
                bankName = invalidBankName,
                amount = amount
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Bank '$invalidBankName' is not available or does not exist") == true)

        // Verify balance hasn't changed
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Initial balance unchanged
    }

    @Test
    fun `should reject instant debit request when amount is zero or negative`() {
        // Arrange
        val receiver = TestUtils.createTestUser("receiver@example.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val bankName = "BBVA"
        val invalidAmount = 0.0

        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                receiverEmail = savedReceiver.email,
                bankName = bankName,
                amount = invalidAmount
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Invalid amount: must be greater than 0") == true)

        // Verify balance hasn't changed
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Initial balance unchanged
    }

    @Test
    fun `should reject instant debit request when amount exceeds limit`() {
        // Arrange
        val receiver = TestUtils.createTestUser("receiver@example.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val bankName = "BBVA"
        val amountExceedingLimit = 100001.0

        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                receiverEmail = savedReceiver.email,
                bankName = bankName,
                amount = amountExceedingLimit
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Instant Debit Request rejected: Amount exceeds the limit") == true)

        // Verify balance hasn't changed
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Initial balance unchanged
    }
}