package billetera_seca.controller

import billetera_seca.model.dto.FakeApiResponse
import billetera_seca.model.dto.InstantDebitRequest
import billetera_seca.service.wallet.WalletService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/wallet")
class WalletController(private val walletService: WalletService) {

    @GetMapping("/balance")
    fun getBalance(@RequestParam email: String): ResponseEntity<Double> {
        return ResponseEntity.ok(walletService.getBalance(email))
    }

    @PostMapping("/transfer")
    fun transfer(
        @RequestParam senderEmail: String,
        @RequestParam receiverEmail: String,
        @RequestParam amount: Double
    ): ResponseEntity<String> {
        walletService.transfer(senderEmail, receiverEmail, amount)
        return ResponseEntity.ok("Transfer successful")
    }

    /**
     * Handles the Instant Debit request by checking if the users exist and if the Instant Debit is approved.
     * If approved, it proceeds with the transfer.
     *
     * User A want to charge money from User B using Instant Debit.
     * A sends a POST to /wallet/instant-debit with the following body: payerEmail, collectorEmail, amount.
     * An external API will be called to check if the Instant Debit is approved. (random answer: success/failure).
     * If the Instant Debit is approved, the transfer will be executed.
     */
    @PostMapping("/instant-debit")
    fun requestInstantDebit(@RequestBody instantDebitRequest: InstantDebitRequest): ResponseEntity<FakeApiResponse<Any>> {
        return walletService.handleInstantDebitRequest(instantDebitRequest)
            .fold(
                onSuccess = {
                    ResponseEntity.ok(
                        FakeApiResponse(
                            success = true,
                            message = "Instant Debit request approved",
                            data = mapOf(
                                "amount" to instantDebitRequest.amount,
                                "bankName" to instantDebitRequest.bankName
                            )
                        )
                    )
                },
                onFailure = { error ->
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        FakeApiResponse(
                            success = false,
                            message = error.message ?: "Unknown error",
                            data = null
                        )
                    )
                }
            )
    }


    @GetMapping("/user-email")
    fun getUserEmailByWalletId(@RequestParam walletId: String): ResponseEntity<String> {
        val email = walletService.getEmailByWalletId(walletId)
        return ResponseEntity.ok(email)
    }
}