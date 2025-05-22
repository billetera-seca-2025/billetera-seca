package billetera_seca.dtoTest

import billetera_seca.model.dto.RegisterRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class RegisterRequestTest {

    @Test
    fun `should create RegisterRequest with correct values`() {
        val request = RegisterRequest(email = "test@example.com", password = "password123")
        assertEquals("test@example.com", request.email)
        assertEquals("password123", request.password)
    }

    @Test
    fun `should verify equality of two identical RegisterRequest objects`() {
        val request1 = RegisterRequest(email = "test@example.com", password = "password123")
        val request2 = RegisterRequest(email = "test@example.com", password = "password123")
        assertEquals(request1, request2)
    }

    @Test
    fun `should verify inequality of two different RegisterRequest objects`() {
        val request1 = RegisterRequest(email = "test@example.com", password = "password123")
        val request2 = RegisterRequest(email = "test@example.com", password = "password456")
        assertNotEquals(request1, request2)
    }
}