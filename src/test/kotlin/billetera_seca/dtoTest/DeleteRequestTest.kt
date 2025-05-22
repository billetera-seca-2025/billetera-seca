package billetera_seca.dtoTest

import billetera_seca.model.dto.DeleteRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class DeleteRequestTest {

    @Test
    fun `should create DeleteRequest with correct values`() {
        val request = DeleteRequest(id = "123")
        assertEquals("123", request.id)
    }

    @Test
    fun `should verify equality of two identical DeleteRequest objects`() {
        val request1 = DeleteRequest(id = "123")
        val request2 = DeleteRequest(id = "123")
        assertEquals(request1, request2)
    }

    @Test
    fun `should verify inequality of two different DeleteRequest objects`() {
        val request1 = DeleteRequest(id = "123")
        val request2 = DeleteRequest(id = "456")
        assertNotEquals(request1, request2)
    }
}