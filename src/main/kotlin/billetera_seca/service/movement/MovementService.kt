package billetera_seca.service.movement

import billetera_seca.model.Movement
import billetera_seca.model.Wallet
import billetera_seca.repository.MovementRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class MovementService(
    private val movementRepository: MovementRepository
) {

    /**
     * Registra un movimiento de salida (outcome) en la billetera.
     * Esto ocurre cuando el usuario envía dinero.
     */
    fun registerOutcome(wallet: Wallet, amount: Double) {
        val movement = Movement(
            wallet = wallet,
            amount = amount,
            type = "outcome",
            createdAt = Date(),
            relatedWalletId = null // En este caso no hay relación, ya que es un egreso simple
        )
        movementRepository.save(movement)
    }

    /**
     * Registra un movimiento de entrada (income) en la billetera.
     * Esto ocurre cuando el usuario recibe dinero.
     */
    fun registerIncome(wallet: Wallet, amount: Double) {
        val movement = Movement(
            wallet = wallet,
            amount = amount,
            type = "income",
            createdAt = Date(),
            relatedWalletId = null // En este caso no hay relación, ya que es una carga desde medio externo
        )
        movementRepository.save(movement)
    }

    /**
     * Registra un movimiento de entrada (income) cuando se realiza una transacción P2P.
     * Esto ocurre cuando un usuario recibe dinero de otro usuario (transacción de persona a persona).
     * El `relatedWalletId` está asociado al wallet del usuario que envió el dinero.
     */
    fun registerIncomeFromP2P(wallet: Wallet, amount: Double, senderWalletId: UUID) {
        val movement = Movement(
            wallet = wallet,
            amount = amount,
            type = "income",
            createdAt = Date(),
            relatedWalletId = senderWalletId // Relacionamos al emisor de la transacción
        )
        movementRepository.save(movement)
    }

    /**
     * Registra un movimiento de salida (outcome) cuando un usuario realiza un pago desde su billetera.
     * Se vincula a un wallet relacionado, en caso de que se trate de una transacción de tipo DEBIN o similares.
     */
    fun registerOutcomeToExternal(wallet: Wallet, amount: Double, relatedWalletId: UUID) {
        val movement = Movement(
            wallet = wallet,
            amount = amount,
            type = "outcome",
            createdAt = Date(),
            relatedWalletId = relatedWalletId // Asociamos al destinatario del pago
        )
        movementRepository.save(movement)
    }
}
