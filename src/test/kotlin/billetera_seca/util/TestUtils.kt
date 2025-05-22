package billetera_seca.util

import billetera_seca.model.User
import billetera_seca.model.Wallet
import java.util.*

object TestUtils {

    // Creates a dummy wallet for testing
    fun createTestWallet(balance: Double = 1000.0): Wallet {
        return Wallet(
            id = UUID.randomUUID(),
            balance = balance
        )
    }

    // Creates a dummy user for testing
    fun createTestUser(email: String = "test@example.com", password: String = "secure123"): User {
        return User(
            id = UUID.randomUUID(),
            email = email,
            password = password,
            wallet = createTestWallet()
        )
    }

    // Creates a user with a specific wallet balance
    fun createTestUserWithBalance(email: String, balance: Double): User {
        return User(
            id = UUID.randomUUID(),
            email = email,
            password = "secure123",
            wallet = createTestWallet(balance)
        )
    }

    // Creates a wallet with a specific ID
    fun createTestWalletWithId(id: UUID, balance: Double = 1000.0): Wallet {
        return Wallet(
            id = id,
            balance = balance
        )
    }
}