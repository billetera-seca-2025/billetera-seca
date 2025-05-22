package billetera_seca.service.transaction

import billetera_seca.exception.NegativeOrZeroAmountException
import billetera_seca.model.Transaction
import billetera_seca.model.TransactionType
import billetera_seca.model.Wallet
import billetera_seca.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository
) {

    /**
     * Registra un movimiento de salida (outcome) en la billetera.
     * Esto ocurre cuando el usuario envía dinero.
     */
    fun registerOutcome(wallet: Wallet, amount: Double) {
        validateAmount(amount)
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
    fun registerIncome(wallet: Wallet, amount: Double) {
        validateAmount(amount)
        val transaction = Transaction(
            wallet = wallet,
            amount = amount,
            type = TransactionType.INCOME,
            createdAt = Date(),
            relatedWalletId = null
        )
        transactionRepository.save(transaction)
    }

    /**
     * Registra un movimiento de entrada (income) cuando se realiza una transacción P2P.
     * Esto ocurre cuando un usuario recibe dinero de otro usuario (transacción de persona a persona).
     * El `relatedWalletId` está asociado al wallet del usuario que envió el dinero.
     */
    fun registerIncomeFromP2P(wallet: Wallet, amount: Double, senderWalletId: UUID) {
        validateAmount(amount)
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
        validateAmount(amount)
        val transaction = Transaction(
            wallet = wallet,
            amount = amount,
            type = TransactionType.OUTCOME,
            createdAt = Date(),
            relatedWalletId = relatedWalletId
        )
        transactionRepository.save(transaction)
    }

    /**
     * Valida que el monto sea mayor a cero.
     * Lanza una excepción si el monto es inválido.
     */
    private fun validateAmount(amount: Double) {
        if (amount <= 0) {
            throw NegativeOrZeroAmountException("Amount must be greater than zero")
        }
    }
}