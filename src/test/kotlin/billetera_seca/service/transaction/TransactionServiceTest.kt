package billetera_seca.service.transaction

import billetera_seca.exception.NegativeOrZeroAmountException
import billetera_seca.model.Transaction
import billetera_seca.model.TransactionType
import billetera_seca.repository.TransactionRepository
import billetera_seca.service.user.UserService
import billetera_seca.util.TestUtils
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TransactionServiceTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var userService: UserService
    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun setUp() {
        transactionRepository = mockk()
        userService = mockk()
        transactionService = TransactionService(transactionRepository)
    }

    @Test
    fun `should register outcome from P2P movement correctly`() {
        val wallet = TestUtils.createTestWallet()
        val amount = 150.0
        val externalWalletId = UUID.randomUUID()

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerOutcomeToExternal(wallet, amount, externalWalletId)

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
        val wallet = TestUtils.createTestWallet()
        val amount = 100.0

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerOutcome(wallet, amount)

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
        val wallet = TestUtils.createTestWallet()
        val amount = 200.0
        val bankName = "BBVA"

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerIncome(wallet, amount)

        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.INCOME && it.relatedWalletId == null && it.relatedBankName == bankName
                }
            )
        }
    }

    @Test
    fun `should throw exception for negative amount in registerOutcome`() {
        val wallet = TestUtils.createTestWallet()
        val negativeAmount = -100.0

        assertThrows<NegativeOrZeroAmountException> {
            transactionService.registerOutcome(wallet, negativeAmount)
        }
    }

    @Test
    fun `should throw exception for zero amount in registerIncome`() {
        val wallet = TestUtils.createTestWallet()
        val zeroAmount = 0.0

        assertThrows<NegativeOrZeroAmountException> {
            transactionService.registerIncome(wallet, zeroAmount)
        }
    }

    @Test
    fun `should handle large amount in registerIncome`() {
        val wallet = TestUtils.createTestWallet()
        val largeAmount = 1_000_000.0

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerIncome(wallet, largeAmount)

        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == largeAmount && it.type == TransactionType.INCOME
                }
            )
        }
    }

    @Test
    fun `should handle incorrect relatedWalletId in registerOutcomeToExternal`() {
        val wallet = TestUtils.createTestWallet()
        val amount = 100.0

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerOutcomeToExternal(wallet, amount, UUID.randomUUID())

        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.OUTCOME && it.relatedWalletId != null
                }
            )
        }
    }

    @Test
    fun `should handle repository save failure`() {
        val wallet = TestUtils.createTestWallet()
        val amount = 100.0

        every { transactionRepository.save(any()) } throws RuntimeException("Database error")

        assertThrows<RuntimeException> {
            transactionService.registerOutcome(wallet, amount)
        }
    }

    @Test
    fun `should assign createdAt date in registerIncome`() {
        val wallet = TestUtils.createTestWallet()
        val amount = 200.0

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerIncome(wallet, amount)

        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.createdAt != null
                }
            )
        }
    }

    @Test
    fun `should throw exception for negative amount in registerIncomeFromP2P`() {
        val wallet = TestUtils.createTestWallet()
        val negativeAmount = -50.0
        val senderWalletId = UUID.randomUUID()

        assertThrows<NegativeOrZeroAmountException> {
            transactionService.registerIncomeFromP2P(wallet, negativeAmount, senderWalletId)
        }
    }

    @Test
    fun `should register income from P2P movement correctly`() {
        val wallet = TestUtils.createTestWallet()
        val amount = 300.0
        val senderWalletId = UUID.randomUUID()

        every { transactionRepository.save(any()) } answers { firstArg<Transaction>() }

        transactionService.registerIncomeFromP2P(wallet, amount, senderWalletId)

        verify(exactly = 1) {
            transactionRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == TransactionType.INCOME && it.relatedWalletId == senderWalletId
                }
            )
        }
    }
}