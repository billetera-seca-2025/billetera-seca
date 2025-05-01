package billetera_seca.service.wallet

import billetera_seca.dto.InstantDebitRequest
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.repository.WalletRepository
import billetera_seca.service.transaction.TransactionService
import billetera_seca.service.user.UserService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient


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
    fun transfer(senderEmail: String, receiverEmail: String, amount: Double){
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

    fun handleInstantDebitRequest(instantDebitRequest: InstantDebitRequest): Boolean {
        userService.findByEmail(instantDebitRequest.payerEmail) ?: throw UserNotFoundException(instantDebitRequest.payerEmail)
        userService.findByEmail(instantDebitRequest.collectorEmail) ?: throw UserNotFoundException(instantDebitRequest.collectorEmail)
        // Call the external fake API to process the Instant Debit request
        val instantDebitApproved = requestInstantDebitAuthorization(instantDebitRequest.amount)
        if (instantDebitApproved) {
            // If the Instant Debit is approved, proceed with the transfer
            transfer(instantDebitRequest.payerEmail, instantDebitRequest.collectorEmail, instantDebitRequest.amount)
            return true
        }
        return false
    }

    private fun requestInstantDebitAuthorization(amount: Double): Boolean {
        // Call the API mock to request Instant Debit authorization
        return webClient.post()
            .uri("/mock/instant-debit")
            .bodyValue(mapOf("amount" to amount))
            .retrieve()
            .bodyToMono(Boolean::class.java)  // Assuming the API returns a boolean (true/false)
            .block() ?: false
    }

}