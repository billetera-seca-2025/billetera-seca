package billetera_seca.service.movement

import billetera_seca.model.Movement
import billetera_seca.repository.MovementRepository
import billetera_seca.util.TestUtils
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class MovementServiceTest {

    private lateinit var movementRepository: MovementRepository
    private lateinit var movementService: MovementService

    @BeforeEach
    fun setUp() {
        movementRepository = mockk()
        movementService = MovementService(movementRepository)
    }

    @Test
    fun `should register outcome from P2P movement correctly`() {
        // Arrange
        val wallet = TestUtils.createTestWallet()
        val amount = 150.0
        val externalWalletId = UUID.randomUUID()

        // Mock the behavior of the repository for the save method
        every {
            movementRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val movement = firstArg<Movement>()
            movement
        }

        // Act
        movementService.registerOutcomeToExternal(wallet, amount, externalWalletId)

        // Assert
        verify(exactly = 1) {
            movementRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == "outcome" && it.relatedWalletId == externalWalletId
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
            movementRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val movement = firstArg<Movement>()
            movement
        }

        // Act
        movementService.registerOutcome(wallet, amount)

        // Assert
        verify(exactly = 1) {
            movementRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == "outcome" && it.relatedWalletId == null
                }
            )
        }
    }

    @Test
    fun `should register income movement correctly`() {
        // Arrange
        val wallet = TestUtils.createTestWallet()
        val amount = 200.0

        // Mock the behavior of the repository for the save method
        every {
            movementRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val movement = firstArg<Movement>()
            movement
        }

        // Act
        movementService.registerIncome(wallet, amount)

        // Assert
        verify(exactly = 1) {
            movementRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == "income" && it.relatedWalletId == null
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
            movementRepository.save(any())
        } answers {
            // Simulates the behavior of saving a movement
            val movement = firstArg<Movement>()
            movement
        }

        // Act
        movementService.registerIncomeFromP2P(wallet, amount, senderWalletId)

        // Assert
        verify(exactly = 1) {
            movementRepository.save(
                match {
                    it.wallet.id == wallet.id && it.amount == amount && it.type == "income" && it.relatedWalletId == senderWalletId
                }
            )
        }
    }

}
