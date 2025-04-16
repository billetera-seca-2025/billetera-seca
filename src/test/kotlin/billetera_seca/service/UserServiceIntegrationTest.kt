package billetera_seca.service

import billetera_seca.BaseTest
import billetera_seca.repository.UserRepository
import billetera_seca.repository.WalletRepository
import billetera_seca.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest: BaseTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `should persist user with wallet`() {
        // Arrange
        val testUser = TestUtils.createTestUser()

        // Act
        val savedUser = userService.createUser(testUser.email, testUser.password, testUser.wallet.balance)

        // Assert
        val userFromDb = userRepository.findById(savedUser.id).get()

        // Make sure the email and wallet are correctly persisted
        assertEquals(testUser.email, userFromDb.email)
        assertEquals(1000.0, userFromDb.wallet.balance)
    }
}