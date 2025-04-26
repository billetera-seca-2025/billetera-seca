package billetera_seca.service

import billetera_seca.exception.InsufficientBalanceException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.repository.WalletRepository
import org.springframework.stereotype.Service

@Service
class WalletService(
    private val walletRepository: WalletRepository,
    private val movementService: MovementService,
    private val userService: UserService
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

        movementService.registerOutcome(senderWallet, amount)
        movementService.registerIncome(receiverWallet, amount)
    }
}