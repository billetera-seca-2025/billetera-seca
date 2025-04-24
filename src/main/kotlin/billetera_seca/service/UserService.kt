package billetera_seca.service

import billetera_seca.exceptions.InvalidEmailFormatException
import billetera_seca.exceptions.UserAlreadyExistsException
import billetera_seca.exceptions.WeakPasswordException
import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository,
) {
    fun createUser(email: String, password: String, initialBalance: Double = 0.0): User {
        if (userRepository.findByEmail(email) != null) {
            throw UserAlreadyExistsException("Email already registered")
        }
        val hashedPassword = hashPassword(password)  // Hash password before saving
        val wallet = walletRepository.save(Wallet(balance = initialBalance))
        val user = User(email = email, password = hashedPassword, wallet = wallet)
        return userRepository.save(user)
    }

    fun validateEmail(email: String) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        if (!email.matches(emailRegex.toRegex())) {
            throw InvalidEmailFormatException("Invalid email format")
        }
    }

    fun validatePassword(password: String) {
        if (password.length < 6) {
            throw WeakPasswordException("Password too short")
        }
    }

    private fun hashPassword(password: String): String {
        val encoder = BCryptPasswordEncoder()
        return encoder.encode(password)
    }
}