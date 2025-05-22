package billetera_seca.modelTest

import billetera_seca.model.User
import billetera_seca.model.Wallet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `should create User with correct values`() {
        val wallet = Wallet()
        val user = User(
            email = "test@example.com",
            password = "password123",
            wallet = wallet
        )

        assertEquals("test@example.com", user.email)
        assertEquals("password123", user.password)
        assertEquals(wallet, user.wallet)
    }

    @Test
    fun `should verify equality of two identical User objects`() {
        val wallet = Wallet()
        val user1 = User(
            email = "test@example.com",
            password = "password123",
            wallet = wallet
        )
        val user2 = User(
            id = user1.id,
            email = "test@example.com",
            password = "password123",
            wallet = wallet
        )
        assertEquals(user1, user2)
    }
}