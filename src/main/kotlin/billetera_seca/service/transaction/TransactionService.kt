package billetera_seca.service.transaction

import billetera_seca.exception.UserNotFoundException
import billetera_seca.model.Transaction
import billetera_seca.model.TransactionType
import billetera_seca.model.Wallet
import billetera_seca.model.dto.TransactionDTO
import billetera_seca.repository.TransactionRepository
import billetera_seca.service.user.UserService
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val userService: UserService
) {

    /**
     * Registra un movimiento de salida (outcome) en la billetera.
     * Esto ocurre cuando el usuario envía dinero.
     */
    fun registerOutcome(wallet: Wallet, amount: Double) {
        val transaction = Transaction(
            wallet = wallet,
            amount = amount,
            type = TransactionType.OUTCOME,
            createdAt = Date(),
            relatedWalletId = null
        )
        transactionRepository.save(transaction)
    }

    /**
     * Registra un movimiento de entrada (income) en la billetera.
     * Esto ocurre cuando el usuario recibe dinero.
     */
    fun registerIncome(wallet: Wallet, amount: Double, bankName: String) {
        val transaction = Transaction(
            wallet = wallet,
            amount = amount,
            type = TransactionType.INCOME,
            createdAt = Date(),
            relatedWalletId = null,
            relatedBankName = bankName
        )
        transactionRepository.save(transaction)
    }

    /**
     * Registra un movimiento de entrada (income) cuando se realiza una transacción P2P.
     * Esto ocurre cuando un usuario recibe dinero de otro usuario (transacción de persona a persona).
     * El `relatedWalletId` está asociado al wallet del usuario que envió el dinero.
     */
    fun registerIncomeFromP2P(wallet: Wallet, amount: Double, senderWalletId: UUID) {
        val transaction = Transaction(
            wallet = wallet,
            amount = amount,
            type = TransactionType.INCOME,
            createdAt = Date(),
            relatedWalletId = senderWalletId
        )
        transactionRepository.save(transaction)
    }

    /**
     * Registra un movimiento de salida (outcome) cuando un usuario realiza un pago desde su billetera.
     * Se vincula a un wallet relacionado, en caso de que se trate de una transacción de tipo DEBIN o similares.
     */
    fun registerOutcomeToExternal(wallet: Wallet, amount: Double, relatedWalletId: UUID) {
        val transaction = Transaction(
            wallet = wallet,
            amount = amount,
            type = TransactionType.OUTCOME,
            createdAt = Date(),
            relatedWalletId = relatedWalletId
        )
        transactionRepository.save(transaction)
    }

    fun getUserTransactionDTOsByEmail(email: String): List<TransactionDTO> {
        val user = userService.findByEmail(email)
            ?: throw UserNotFoundException(email)

        return user.wallet.transactions
            .sortedByDescending { it.createdAt }
            .map {
                TransactionDTO(
                    amount = it.amount,
                    type = it.type,
                    createdAt = it.createdAt,
                    relatedWalletId = it.relatedWalletId,
                    relatedBankName = it.relatedBankName
                )
            }
    }

}
