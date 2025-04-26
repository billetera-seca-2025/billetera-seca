package billetera_seca.service.wallet

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


@Service
class WalletService(
    private val walletRepository: WalletRepository,
    private val transactionService: TransactionService,
    private val userService: UserService,
    private val restTemplate: RestTemplate
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
    /*
    Simulates recharging balance using a fake API: when the user makes a transaction from a bank account or card to the wallet.
    In this scenario, the money is simply transferred from the payment system to the user's wallet.
     */
    fun rechargeBalance(email: String, amount: Double) {
        val user = userService.findByEmail(email) ?: throw UserNotFoundException(email)

        // Prepare the request to the fake API
        val url = "http://fake-api.com/wallet/recharge-balance"
        val requestHeaders = HttpHeaders()
        requestHeaders.set("Content-Type", "application/json")
        val requestBody = mapOf("amount" to amount)
        val requestEntity = HttpEntity(requestBody, requestHeaders)

        // Send the request to the fake API
        val response: ResponseEntity<FakeApiResponse> = restTemplate.exchange(url, HttpMethod.POST, requestEntity, FakeApiResponse::class.java)

        // Check the response from the fake API
        if (response.body?.success == true) {
            user.wallet.balance += amount
            walletRepository.save(user.wallet)
        } else {
            throw Exception("Error al cargar saldo.")
        }
    }

    /*
    The frontend (either the web or mobile app) makes an application to perform a debit from a bank account to the user's wallet.
    The fake API will be called to simulate this transfer of funds from the bank account to the wallet system.
     */
    //TODO: Implement the debin() method
}