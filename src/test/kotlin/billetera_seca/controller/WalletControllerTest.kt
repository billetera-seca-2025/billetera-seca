package billetera_seca.controller

import billetera_seca.model.dto.InstantDebitRequest
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.service.wallet.WalletService
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus

class WalletControllerTest {

    private lateinit var walletService: WalletService
    private lateinit var walletController: WalletController

    @BeforeEach
    fun setUp() {
        walletService = mockk()
        walletController = WalletController(walletService)
    }

    @Test
    fun `getBalance should return wallet balance`() {
        // Arrange
        val email = "user@example.com"
        val expectedBalance = 1000.0

        every { walletService.getBalance(email) } returns expectedBalance

        // Act
        val response = walletController.getBalance(email)

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == expectedBalance)
        verify(exactly = 1) { walletService.getBalance(email) }
    }

    @Test
    fun `transfer should return success when transfer is successful`() {
        // Arrange
        val senderEmail = "sender@example.com"
        val receiverEmail = "receiver@example.com"
        val amount = 100.0

        every { walletService.transfer(senderEmail, receiverEmail, amount) } just runs

        // Act
        val response = walletController.transfer(senderEmail, receiverEmail, amount)

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == "Transfer successful")
        verify(exactly = 1) { walletService.transfer(senderEmail, receiverEmail, amount) }
    }

    @Test
    fun `transfer should propagate UserNotFoundException when user not found`() {
        // Arrange
        val senderEmail = "nonexistent@example.com"
        val receiverEmail = "receiver@example.com"
        val amount = 100.0

        every { walletService.transfer(senderEmail, receiverEmail, amount) } throws UserNotFoundException("User not found")

        // Act & Assert
        assertThrows<UserNotFoundException> {
            walletController.transfer(senderEmail, receiverEmail, amount)
        }
        verify(exactly = 1) { walletService.transfer(senderEmail, receiverEmail, amount) }
    }

    @Test
    fun `transfer should propagate InsufficientBalanceException when balance is insufficient`() {
        // Arrange
        val senderEmail = "sender@example.com"
        val receiverEmail = "receiver@example.com"
        val amount = 5000.0 // More than the balance

        every { walletService.transfer(senderEmail, receiverEmail, amount) } throws InsufficientBalanceException("Insufficient balance")

        // Act & Assert
        assertThrows<InsufficientBalanceException> {
            walletController.transfer(senderEmail, receiverEmail, amount)
        }
        verify(exactly = 1) { walletService.transfer(senderEmail, receiverEmail, amount) }
    }

    @Test
    fun `requestInstantDebit should return OK when DEBIN is accepted`() {
        // Arrange
        val debinRequest = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "BBVA",
            amount = 200.0
        )

        every { walletService.handleInstantDebitRequest(debinRequest) } returns Result.success(true)

        // Act
        val response = walletController.requestInstantDebit(debinRequest)

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.success == true)
        assert(response.body?.message == "Instant Debit request approved")
        verify(exactly = 1) { walletService.handleInstantDebitRequest(debinRequest) }
    }

    @Test
    fun `requestInstantDebit should return BAD_REQUEST when DEBIN is rejected`() {
        // Arrange
        val instantDebitRequest = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "BBVA",
            amount = 200.0
        )

        every { walletService.handleInstantDebitRequest(instantDebitRequest) } returns Result.failure(RuntimeException("Bank rejected the operation"))

        // Act
        val response = walletController.requestInstantDebit(instantDebitRequest)

        // Assert
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
        assert(response.body?.success == false)
        assert(response.body?.message == "Bank rejected the operation")
        verify(exactly = 1) { walletService.handleInstantDebitRequest(instantDebitRequest) }
    }
}