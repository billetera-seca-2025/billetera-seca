package billetera_seca.dtoTest

import billetera_seca.model.dto.LoginRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class LoginRequestTest {

    @Test
    fun `should create LoginRequest with correct values`() {
        val request = LoginRequest(email = "test@example.com", password = "password123")
        assertEquals("test@example.com", request.email)
        assertEquals("password123", request.password)
    }

    @Test
    fun `should verify equality of two identical LoginRequest objects`() {
        val request1 = LoginRequest(email = "test@example.com", password = "password123")
        val request2 = LoginRequest(email = "test@example.com", password = "password123")
        assertEquals(request1, request2)
    }

    @Test
    fun `should verify inequality of two different LoginRequest objects`() {
        val request1 = LoginRequest(email = "test@example.com", password = "password123")
        val request2 = LoginRequest(email = "test@example.com", password = "password456")
        assertNotEquals(request1, request2)
    }
}