package billetera_seca.controller

import billetera_seca.service.WalletService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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
}