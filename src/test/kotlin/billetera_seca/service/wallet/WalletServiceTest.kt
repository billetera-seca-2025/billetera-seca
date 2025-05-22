package billetera_seca.service.wallet

import billetera_seca.dto.InstantDebitRequest
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.NegativeOrZeroAmountException
import billetera_seca.exception.SelfTransferException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.model.Wallet
import billetera_seca.repository.WalletRepository
import billetera_seca.service.transaction.TransactionService
import billetera_seca.service.user.UserService
import billetera_seca.util.TestUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.*
import org.junit.jupiter.api.assertThrows
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class WalletServiceTest {

    private lateinit var walletRepository: WalletRepository
    private lateinit var transactionService: TransactionService
    private lateinit var userService: UserService
    private lateinit var walletService: WalletService
    private lateinit var webClient: WebClient

    @BeforeEach
    fun setUp() {
        walletRepository = mockk()
        transactionService = mockk()
        userService = mockk()
        webClient = mockk()
        walletService = WalletService(walletRepository, transactionService, userService, webClient)
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
        every { transactionService.registerOutcomeToExternal(any(), any(), any()) } just runs
        every { transactionService.registerIncomeFromP2P(any(), any(), any()) } just runs

        // Act
        walletService.transfer(senderEmail, receiverEmail, amount)

        // Assert
        assertEquals(100.0, sender.wallet.balance)  // 200 - 100
        assertEquals(150.0, receiver.wallet.balance)  // 50 + 100

        verify(exactly = 1) { walletRepository.save(sender.wallet) }
        verify(exactly = 1) { walletRepository.save(receiver.wallet) }
        verify(exactly = 1) { transactionService.registerOutcomeToExternal(sender.wallet, amount, any()) }
        verify(exactly = 1) { transactionService.registerIncomeFromP2P(receiver.wallet, amount, any()) }
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
        verify(exactly = 0) { transactionService.registerOutcome(any(), any()) }
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
        verify(exactly = 0) { transactionService.registerOutcome(any(), any()) }
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
        verify(exactly = 0) { transactionService.registerOutcome(any(), any()) }
    }

    @Test
    fun `handleInstantDebitRequest should process instant debit request and transfer funds`() {
        // Arrange
        val payerEmail = "email@gmail.com"
        val collectorEmail = "email2@gmail.com"
        val amount = 100.0
        val instantDebitRequest = InstantDebitRequest(payerEmail, collectorEmail, amount)
        val payer = TestUtils.createTestUser(payerEmail).apply {
            wallet.balance = 200.0
        }
        val collector = TestUtils.createTestUser(collectorEmail).apply {
            wallet.balance = 50.0
        }

        every { userService.findByEmail(payerEmail) } returns payer
        every { userService.findByEmail(collectorEmail) } returns collector
        every {
            webClient.post().uri("/mock/instant-debit").bodyValue(any()).retrieve().bodyToMono(Boolean::class.java)
        } returns Mono.just(true)
        every { walletRepository.save(any()) } answers { firstArg<Wallet>() }
        every { transactionService.registerOutcomeToExternal(any(), any(), any()) } just runs
        every { transactionService.registerIncomeFromP2P(any(), any(), any()) } just runs
        // Act
        val result = walletService.handleInstantDebitRequest(instantDebitRequest)
        // Assert
        assertEquals(true, result)
        assertEquals(100.0, payer.wallet.balance)  // 200 - 100
        assertEquals(150.0, collector.wallet.balance)  // 50 + 100
        verify(exactly = 1) { walletRepository.save(payer.wallet) }
        verify(exactly = 1) { walletRepository.save(collector.wallet) }
        verify(exactly = 1) { transactionService.registerOutcomeToExternal(payer.wallet, amount, collector.wallet.id) }
        verify(exactly = 1) { transactionService.registerIncomeFromP2P(collector.wallet, amount, payer.wallet.id) }
    }

    @Test
    fun `handleInstantDebitRequest should throw UserNotFoundException when payer not found`() {
        // Arrange
        val payerEmail = "email@gmail.com"
        val collectorEmail = "email2@gmail.com"
        val amount = 100.0
        val instantDebitRequest = InstantDebitRequest(payerEmail, collectorEmail, amount)
        every { userService.findByEmail(payerEmail) } returns null
        every { userService.findByEmail(collectorEmail) } returns TestUtils.createTestUser(collectorEmail)
        // Act & Assert
        assertThrows<UserNotFoundException> {
            walletService.handleInstantDebitRequest(instantDebitRequest)
        }
        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { transactionService.registerOutcomeToExternal(any(), any(), any()) }
        verify(exactly = 0) { transactionService.registerIncomeFromP2P(any(), any(), any()) }
        verify(exactly = 0) {
            webClient.post().uri("/mock/instant-debit").bodyValue(any()).retrieve().bodyToMono(Boolean::class.java)
        }
    }

    @Test
    fun `handleInstantDebitRequest should throw UserNotFoundException when collector not found`() {
        // Arrange
        val payerEmail = "email@gmail.com"
        val collectorEmail = "email2@gmail.com"
        val amount = 100.0
        val instantDebitRequest = InstantDebitRequest(payerEmail, collectorEmail, amount)
        every { userService.findByEmail(payerEmail) } returns TestUtils.createTestUser(payerEmail)
        every { userService.findByEmail(collectorEmail) } returns null
        // Act & Assert
        assertThrows<UserNotFoundException> {
            walletService.handleInstantDebitRequest(instantDebitRequest)
        }
        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { transactionService.registerOutcomeToExternal(any(), any(), any()) }
        verify(exactly = 0) { transactionService.registerIncomeFromP2P(any(), any(), any()) }
        verify(exactly = 0) {
            webClient.post().uri("/mock/instant-debit").bodyValue(any()).retrieve().bodyToMono(Boolean::class.java)
        }
    }

    @Test
    fun `handleInstantDebitRequest should not transfer funds if instant debit is not approved`() {
        // Arrange
        val payerEmail = "email@gmail.com"
        val collectorEmail = "email2@gmail.com"
        val amount = 100.0
        val instantDebitRequest = InstantDebitRequest(payerEmail, collectorEmail, amount)
        val payer = TestUtils.createTestUser(payerEmail).apply {
            wallet.balance = 200.0
        }
        val collector = TestUtils.createTestUser(collectorEmail).apply {
            wallet.balance = 50.0
        }
        every { userService.findByEmail(payerEmail) } returns payer
        every { userService.findByEmail(collectorEmail) } returns collector
        every {
            webClient.post().uri("/mock/instant-debit").bodyValue(any()).retrieve().bodyToMono(Boolean::class.java)
        } returns Mono.just(false)
        every { walletRepository.save(any()) } answers { firstArg<Wallet>() }
        every { transactionService.registerOutcomeToExternal(any(), any(), any()) } just runs
        every { transactionService.registerIncomeFromP2P(any(), any(), any()) } just runs
        // Act
        val result = walletService.handleInstantDebitRequest(instantDebitRequest)
        // Assert
        assertEquals(false, result)
        assertEquals(200.0, payer.wallet.balance)  // No change
        assertEquals(50.0, collector.wallet.balance)  // No change
        verify(exactly = 0) { walletRepository.save(payer.wallet) }
        verify(exactly = 0) { walletRepository.save(collector.wallet) }
        verify(exactly = 0) { transactionService.registerOutcomeToExternal(payer.wallet, amount, collector.wallet.id) }
        verify(exactly = 0) { transactionService.registerIncomeFromP2P(collector.wallet, amount, payer.wallet.id) }
        verify(exactly = 1) {
            webClient.post().uri("/mock/instant-debit").bodyValue(any()).retrieve().bodyToMono(Boolean::class.java)
        }
    }

    @Test
    fun `getBalance should throw UserNotFoundException if user is not found`() {
        // Arrange
        val email = "nonexistent@example.com"
        every { userService.findByEmail(email) } returns null

        // Act & Assert
        assertThrows<UserNotFoundException> {
            walletService.getBalance(email)
        }
        verify(exactly = 1) { userService.findByEmail(email) }
    }

    @Test
    fun `addBalance should successfully add funds to a user's wallet`() {
        // Arrange
        val email = "user@example.com"
        val amount = 100.0
        val user = TestUtils.createTestUser(email).apply {
            wallet.balance = 200.0
        }
        every { userService.findByEmail(email) } returns user
        every { walletRepository.save(any()) } answers { firstArg<Wallet>() }

        // Act
        walletService.addBalance(email, amount)

        // Assert
        assertEquals(300.0, user.wallet.balance)  // 200 + 100
        verify(exactly = 1) { walletRepository.save(user.wallet) }
    }

    @Test
    fun `addBalance should throw NegativeOrZeroAmountException if amount is zero or negative`() {
        // Arrange
        val email = "user@example.com"
        val user = TestUtils.createTestUser(email)
        every { userService.findByEmail(email) } returns user

        // Act & Assert
        assertThrows<NegativeOrZeroAmountException> {
            walletService.addBalance(email, 0.0)
        }
        assertThrows<NegativeOrZeroAmountException> {
            walletService.addBalance(email, -50.0)
        }
        verify(exactly = 0) { walletRepository.save(any()) }
    }

    @Test
    fun `transfer should throw SelfTransferException if sender and receiver are the same`() {
        // Arrange
        val email = "user@example.com"
        val amount = 100.0
        val user = TestUtils.createTestUser(email).apply {
            wallet.balance = 200.0
        }
        every { userService.findByEmail(email) } returns user

        // Act & Assert
        assertThrows<SelfTransferException> {
            walletService.transfer(email, email, amount)
        }
        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { transactionService.registerOutcome(any(), any()) }
    }
/*
    @Test
    fun `handleInstantDebitRequest should throw NegativeOrZeroAmountException for zero or negative amounts`() {
        // Arrange
        val payerEmail = "payer@example.com"
        val collectorEmail = "collector@example.com"
        val instantDebitRequestZero = InstantDebitRequest(payerEmail, collectorEmail, 0.0)
        val instantDebitRequestNegative = InstantDebitRequest(payerEmail, collectorEmail, -100.0)

        every { userService.findByEmail(payerEmail) } returns TestUtils.createTestUser(payerEmail)
        every { userService.findByEmail(collectorEmail) } returns TestUtils.createTestUser(collectorEmail)

        // Act & Assert
        assertThrows<NegativeOrZeroAmountException> {
            walletService.handleInstantDebitRequest(instantDebitRequestZero)
        }
        assertThrows<NegativeOrZeroAmountException> {
            walletService.handleInstantDebitRequest(instantDebitRequestNegative)
        }
        verify(exactly = 0) { walletRepository.save(any()) }
        verify(exactly = 0) { transactionService.registerOutcomeToExternal(any(), any(), any()) }
        verify(exactly = 0) { transactionService.registerIncomeFromP2P(any(), any(), any()) }
    }

 */

}