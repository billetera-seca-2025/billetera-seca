package billetera_seca.service.movement

import billetera_seca.model.Movement
import billetera_seca.model.Wallet
import billetera_seca.repository.MovementRepository
import org.springframework.stereotype.Service

@Service
class MovementService(private val movementRepository: MovementRepository) {

    fun registerIncome(wallet: Wallet, amount: Double) {
        val movement = Movement(wallet = wallet, amount = amount, type = "income")
        movementRepository.save(movement)
    }

    fun registerOutcome(wallet: Wallet, amount: Double) {
        val movement = Movement(wallet = wallet, amount = amount, type = "outcome")
        movementRepository.save(movement)
    }
}