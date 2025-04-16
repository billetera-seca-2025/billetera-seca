package billetera_seca.util

import billetera_seca.model.User
import billetera_seca.model.Wallet
import java.util.*

object TestUtils {

    // Creates a dummy wallet for testing
    fun createTestWallet(): Wallet {
        return Wallet(
            id = UUID.randomUUID(),
            balance = 1000.0
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
}