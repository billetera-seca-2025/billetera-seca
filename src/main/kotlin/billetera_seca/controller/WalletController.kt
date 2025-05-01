package billetera_seca.controller

import billetera_seca.dto.InstantDebitRequest
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
     * Handles the DEBIN request by checking if the users exist and if the DEBIN is approved.
     * If approved, it proceeds with the transfer.
     *
     * User A want to charge money from User B using DEBIN.
     * A sends a POST to /wallet/debin with the following body: payerEmail, collectorEmail, amount.
     * An external API will be called to check if the DEBIN is approved. (random answer: success/failure).
     * If the DEBIN is approved, the transfer will be executed.
     */
    @PostMapping("/debin")
    fun requestDebin(@RequestBody instantDebitRequest: InstantDebitRequest): ResponseEntity<String> {
        val result = walletService.handleInstantDebitRequest(instantDebitRequest)
        return if (result) {
            ResponseEntity.ok("DEBIN accepted and processed")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DEBIN rejected or failed")
        }
    }
}