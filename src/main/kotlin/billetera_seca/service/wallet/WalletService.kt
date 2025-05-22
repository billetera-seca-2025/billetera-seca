package billetera_seca.service.wallet

import billetera_seca.model.dto.InstantDebitRequest
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.exception.WalletNotFoundException
import billetera_seca.repository.WalletRepository
import billetera_seca.service.transaction.TransactionService
import billetera_seca.service.user.UserService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID


@Service
class WalletService(
    private val walletRepository: WalletRepository,
    private val transactionService: TransactionService,
    private val userService: UserService,
    private val webClient: WebClient
) {
    fun getBalance(email: String): Double {
        val user = userService.findByEmail(email) ?: throw UserNotFoundException(email)
        return user.wallet.balance
    }

    fun getEmailByWalletId(walletId: String): String {
        val wallet = walletRepository.findById(UUID.fromString(walletId))
            .orElseThrow { WalletNotFoundException("Wallet not found") }
        return wallet.user?.email ?: throw UserNotFoundException("User not found for wallet ID: $walletId")
    }

    fun transfer(senderEmail: String, receiverEmail: String, amount: Double) {
        val sender = userService.findByEmail(senderEmail) ?: throw UserNotFoundException(senderEmail)
        val receiver = userService.findByEmail(receiverEmail) ?: throw UserNotFoundException(receiverEmail)

        val senderWallet = sender.wallet
        val receiverWallet = receiver.wallet

        if (senderWallet.balance < amount) {
            throw InsufficientBalanceException("Insufficient funds")
        }

        senderWallet.balance -= amount
        receiverWallet.balance += amount

        walletRepository.save(sender.wallet)
        walletRepository.save(receiver.wallet)

        transactionService.registerOutcomeToExternal(senderWallet, amount, receiverWallet.id)
        transactionService.registerIncomeFromP2P(receiverWallet, amount, senderWallet.id)
    }

    fun handleInstantDebitRequest(instantDebitRequest: InstantDebitRequest): Result<Boolean> {
        println("Received instant debit request: $instantDebitRequest")

        return try {

            // Validate if user exists
            userService.findByEmail(instantDebitRequest.receiverEmail)
                ?: throw UserNotFoundException(instantDebitRequest.receiverEmail)

            // Call the external fake API to validate bank and process the Instant Debit request
            val instantDebitResult = requestInstantDebitAuthorization(
                receiverEmail = instantDebitRequest.receiverEmail,
                bankName = instantDebitRequest.bankName,
                amount = instantDebitRequest.amount
            )

            when {
                instantDebitResult.isSuccess && instantDebitResult.getOrDefault(false) -> {
                    // Procesar la transferencia dentro de una transacción
                    try {
                        transferInstantDebit(instantDebitRequest.receiverEmail, instantDebitRequest.amount, instantDebitRequest.bankName)
                        Result.success(true)
                    } catch (e: Exception) {
                        // Si falla la transferencia, registrar el error y propagar
                        println("Error processing transfer after successful authorization: ${e.message}")
                        Result.failure(e)
                    }
                }
                instantDebitResult.isFailure -> {
                    // Propagar el error específico del banco
                    Result.failure(instantDebitResult.exceptionOrNull() ?: RuntimeException("El banco rechazó la operación"))
                }
                else -> {
                    Result.failure(RuntimeException("El banco rechazó la operación"))
                }
            }
        } catch (e: Exception) {
            when (e) {
                is UserNotFoundException -> Result.failure(e)
                is IllegalArgumentException -> Result.failure(e)
                else -> {
                    println("Unexpected error processing instant debit: ${e.message}")
                    Result.failure(RuntimeException("Error inesperado al procesar el DEBIN"))
                }
            }
        }
    }

    private fun transferInstantDebit(receiverEmail: String, amount: Double, bankName: String) {
        val receiver = userService.findByEmail(receiverEmail)
            ?: throw UserNotFoundException(receiverEmail)

        val receiverWallet = receiver.wallet
        receiverWallet.balance += amount

        try {
            walletRepository.save(receiver.wallet)
            transactionService.registerIncome(receiverWallet, amount, bankName)
        } catch (e: Exception) {
            // If the transfer fails, revert the balance change
            receiverWallet.balance -= amount
            throw RuntimeException("Error at processing transference: ${e.message}")
        }
    }

    private fun requestInstantDebitAuthorization(
        receiverEmail: String,
        bankName: String,
        amount: Double
    ): Result<Boolean> {
        // Call the API mock to request Instant Debit authorization
        return try {
            val response = webClient.post()
                .uri("/mock/instant-debit")
                .bodyValue(InstantDebitRequest(receiverEmail = receiverEmail, bankName = bankName, amount = amount))
                .retrieve()
                .onStatus({ it.isError }) { response ->
                    response.bodyToMono(String::class.java).flatMap { body ->
                        // Log the specific error response
                        throw RuntimeException(body)
                    }
                }
                .bodyToMono(Boolean::class.java)
                .block()

            Result.success(response ?: false)
        } catch (ex: Exception) {
            // Handle any errors that occur during the API call
            println("Error communicating with bank API: ${ex.message}")
            Result.failure(RuntimeException(ex.message))
        }
    }
}