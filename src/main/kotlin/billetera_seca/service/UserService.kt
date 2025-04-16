package billetera_seca.service

import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository,
) {
    fun createUser(email: String, password: String, initialBalance: Double = 0.0): User {
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("Email already registered")
        }
        val wallet = walletRepository.save(Wallet(balance = initialBalance))
        val user = User(email = email, password = password, wallet = wallet)
        return userRepository.save(user)
    }

}