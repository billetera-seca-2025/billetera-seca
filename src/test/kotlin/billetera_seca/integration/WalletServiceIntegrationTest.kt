package billetera_seca.integration

import billetera_seca.BaseTest
import billetera_seca.config.TestWebClientConfig
import billetera_seca.exception.InsufficientBalanceException
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
import billetera_seca.model.dto.InstantDebitRequest
import org.junit.jupiter.api.Tag

@Tag("ci-exclude")
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
                amount = amount,
                cbu = "1234567890123456789012"
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Bank $invalidBankName is not available or does not exist") == true)
        
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
                amount = invalidAmount,
                cbu = "1234567890123456789012"
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Invalid amount, must be greater than 0") == true)
        
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
                amount = amountExceedingLimit,
                cbu = "1234567890123456789012"
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Instant Debit Request rejected: Amount exceeds limit") == true)
        
        // Verify balance hasn't changed
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Initial balance unchanged
    }

    @Test
    fun `should reject instant debit request when CBU does not exist`() {
        // Arrange
        val receiver = TestUtils.createTestUser("receiver@example.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val bankName = "BBVA"
        val amount = 100.0
        val nonExistentCbu = "9999999999999999999999" // CBU that does not exist

        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                receiverEmail = savedReceiver.email,
                bankName = bankName,
                amount = amount,
                cbu = nonExistentCbu
            )
        )
        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("CBU $nonExistentCbu does not exist") == true)

        // Verify balance hasn't changed
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Initial balance unchanged

        // Verify CBU balance hasn't changed
        val cbuBalance = walletService.getCbuBalance(nonExistentCbu)
        assertEquals(null, cbuBalance) // CBU balance should be null since it does not exist
    }

    @Test
    fun `should reject instant debit request when insufficient balance from CBU`() {
        // Arrange
        val receiver = TestUtils.createTestUser("receiver@example.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val bankName = "BBVA"
        val amount = 150000.0 // Amount greater than the initial balance of the CBU, which is 100000.0
        val cbuWithInsufficientBalance = "3456789012345678901234" // CBU with insufficient balance

        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                receiverEmail = savedReceiver.email,
                bankName = bankName,
                amount = amount,
                cbu = cbuWithInsufficientBalance
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isFailure)
        assert(result.exceptionOrNull()?.message?.contains("Insufficient balance. Current balance: 100000.0, Required: 150000.0") == true)

        // Verify balance hasn't changed
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0, updatedReceiver.wallet.balance) // Initial balance unchanged

        // Verify CBU balance hasn't changed
        val cbuBalance = walletService.getCbuBalance(cbuWithInsufficientBalance)
        assertEquals(100000.0, cbuBalance) // CBU balance remains unchanged
    }

    @Test
    fun `should process valid instant debit request successfully`() {
        // Arrange
        val receiver = TestUtils.createTestUser("receiver@example.com")
        val savedReceiver = userService.createUser(receiver.email, receiver.password, receiver.wallet.balance)
        val bankName = "BBVA"
        val amount = 30000.0 // Valid amount within limits
        val cbuWithSufficientBalance = "7890123456789012345678" // CBU with sufficient balance -> 45000.0

        // Act
        val result = walletService.handleInstantDebitRequest(
            InstantDebitRequest(
                receiverEmail = savedReceiver.email,
                bankName = bankName,
                amount = amount,
                cbu = cbuWithSufficientBalance
            )
        )

        // Debug: Print detailed result information
        println("Result isSuccess: ${result.isSuccess}")
        println("Result isFailure: ${result.isFailure}")
        println("Result exception: ${result.exceptionOrNull()}")
        println("Result exception message: ${result.exceptionOrNull()?.message}")

        // Assert
        assert(result.isSuccess)
        assert(result.getOrNull() == true) // Should return true on success

        // Verify the receiver's balance has been updated correctly
        val updatedReceiver = userRepository.findById(savedReceiver.id).get()
        assertEquals(1000.0 + amount, updatedReceiver.wallet.balance) // Initial balance + amount

        // Verify the CBU balance has been updated correctly
        val cbuBalance = walletService.getCbuBalance(cbuWithSufficientBalance)
        assertEquals(45000.0 - amount, cbuBalance) // Initial CBU balance - amount

    }
}