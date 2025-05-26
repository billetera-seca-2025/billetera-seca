package billetera_seca.service.transaction

import billetera_seca.model.Transaction
import billetera_seca.model.TransactionType
import billetera_seca.repository.TransactionRepository
import billetera_seca.service.user.UserService
import billetera_seca.util.TestUtils
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TransactionServiceTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var userService: UserService
    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun setUp() {
        transactionRepository = mockk()
        userService = mockk()
        transactionService = TransactionService(transactionRepository, userService)
    }

    @Test
    fun `should register outcome from P2P movement correctly`() {
        // Arrange
        val wallet = TestUtils.createTestWallet()
        val amount = 150.0
        val externalWalletId = UUID.randomUUID()

        // Mock the behavior of the repository for the save method
        every {
            transactionRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val transaction = firstArg<Transaction>()
            transaction
        }

        // Act
        transactionService.registerOutcomeToExternal(wallet, amount, externalWalletId)

        // Assert
        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.OUTCOME && it.relatedWalletId == externalWalletId
                }
            )
        }
    }

    @Test
    fun `should register outcome movement correctly`() {
        // Arrange
        val wallet = TestUtils.createTestWallet()
        val amount = 100.0

        // Mock the behavior of the repository for the save method
        every {
            transactionRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val transaction = firstArg<Transaction>()
            transaction
        }

        // Act
        transactionService.registerOutcome(wallet, amount)

        // Assert
        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.OUTCOME && it.relatedWalletId == null
                }
            )
        }
    }

    @Test
    fun `should register income movement correctly`() {
        // Arrange
        val wallet = TestUtils.createTestWallet()
        val amount = 200.0
        val bankName = "BBVA"

        // Mock the behavior of the repository for the save method
        every {
            transactionRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val transaction = firstArg<Transaction>()
            transaction
        }

        // Act
        transactionService.registerIncome(wallet, amount, bankName)

        // Assert
        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.INCOME && it.relatedWalletId == null && it.relatedBankName == bankName
                }
            )
        }
    }

    @Test
    fun `should register income from P2P movement correctly`() {
        // Arrange
        val wallet = TestUtils.createTestWallet()
        val amount = 300.0
        val senderWalletId = UUID.randomUUID()

        // Mock the behavior of the repository for the save method
        every {
            transactionRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val transaction = firstArg<Transaction>()
            transaction
        }

        // Act
        transactionService.registerIncomeFromP2P(wallet, amount, senderWalletId)

        // Assert
        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.INCOME && it.relatedWalletId == senderWalletId
                }
            )
        }
    }
}
