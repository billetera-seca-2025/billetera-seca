package billetera_seca.service.wallet

import billetera_seca.dto.DebinRequest
import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.model.dto.FakeApiResponse
import billetera_seca.repository.WalletRepository
import billetera_seca.service.transaction.TransactionService
import billetera_seca.service.user.UserService
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
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

    fun handleDebinRequest(debinRequest: DebinRequest): Boolean {
        userService.findByEmail(debinRequest.payerEmail) ?: throw UserNotFoundException(debinRequest.payerEmail)
        userService.findByEmail(debinRequest.collectorEmail) ?: throw UserNotFoundException(debinRequest.collectorEmail)
        // Call the external fake API to process the DEBIN request
        val debinApproved = requestDebinAuthorization(debinRequest.amount)
        if (debinApproved) {
            // If the DEBIN is approved, proceed with the transfer
            transfer(debinRequest.payerEmail, debinRequest.collectorEmail, debinRequest.amount)
            return true
        }
        return false
    }

    private fun requestDebinAuthorization(amount: Double): Boolean {
        // Call the API mock to request DEBIN authorization
        return webClient.post()
            .uri("/mock/debin")
            .bodyValue(mapOf("amount" to amount))
            .retrieve()
            .bodyToMono(Boolean::class.java)  // Assuming the API returns a boolean (true/false)
            .block() ?: false
    }

}