package billetera_seca.dtoTest

import billetera_seca.model.dto.UpdateRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class UpdateRequestTest {

    @Test
    fun `should create UpdateRequest with correct values`() {
        val request = UpdateRequest(
            id = "1",
            name = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123"
        )
        assertEquals("1", request.id)
        assertEquals("John", request.name)
        assertEquals("Doe", request.lastName)
        assertEquals("john.doe@example.com", request.email)
        assertEquals("password123", request.password)
    }

    @Test
    fun `should verify equality of two identical UpdateRequest objects`() {
        val request1 = UpdateRequest(
            id = "1",
            name = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123"
        )
        val request2 = UpdateRequest(
            id = "1",
            name = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123"
        )
        assertEquals(request1, request2)
    }

    @Test
    fun `should verify inequality of two different UpdateRequest objects`() {
        val request1 = UpdateRequest(
            id = "1",
            name = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123"
        )
        val request2 = UpdateRequest(
            id = "2",
            name = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            password = "password456"
        )
        assertNotEquals(request1, request2)
    }
}