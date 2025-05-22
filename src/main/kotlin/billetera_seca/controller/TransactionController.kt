package billetera_seca.controller

import billetera_seca.model.dto.TransactionDTO
import billetera_seca.service.transaction.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService,
) {
    /**
     * Lista todas las transacciones de un usuario, dado su email.
     */
    @GetMapping
    fun getUserTransactions(@RequestParam email: String): ResponseEntity<List<TransactionDTO>> {
        return try {
            val transactions = transactionService.getUserTransactionDTOsByEmail(email)
            ResponseEntity.ok(transactions)
        } catch (e: Exception) {
            ResponseEntity.status(400).body(null)
        }
    }
}
