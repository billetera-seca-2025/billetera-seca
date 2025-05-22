package billetera_seca.service.user

import billetera_seca.exception.UserAlreadyExistsException
import billetera_seca.exception.UserNotFoundException
import billetera_seca.model.User
import billetera_seca.model.Wallet
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import billetera_seca.util.UserValidator
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val walletRepository: WalletRepository,
) {
    fun createUser(email: String, password: String, initialBalance: Double = 50000.0): User {
        UserValidator.validateEmail(email)
        UserValidator.validatePassword(password)
        if (userRepository.findByEmail(email) != null) {
            throw UserAlreadyExistsException("Email already registered")
        }
        val hashedPassword = hashPassword(password)  // Hash password before saving
        val wallet = walletRepository.save(Wallet(balance = initialBalance))
        val user = User(email = email, password = hashedPassword, wallet = wallet)
        return userRepository.save(user)
    }

    fun updateUser(user: User): User {
        val existingUser = userRepository.findById(user.id).orElseThrow {
            throw UserNotFoundException("User not found")
        }
        existingUser.email = user.email
        existingUser.password = hashPassword(user.password)  // Hash password before saving
        return userRepository.save(existingUser)
    }

    fun deleteUser(userId: UUID) {
        val user = userRepository.findById(userId).orElseThrow {
            throw UserNotFoundException("User not found")
        }
        userRepository.delete(user)
    }

    private fun hashPassword(password: String): String {
        val encoder = BCryptPasswordEncoder()
        return encoder.encode(password)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun findById(id: UUID): User? {
        return userRepository.findById(id).orElse(null)
    }
}