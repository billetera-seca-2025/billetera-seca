package billetera_seca.dtoTest

import billetera_seca.model.dto.FakeApiResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class FakeApiResponseTest {

    @Test
    fun `should create FakeApiResponse with correct values`() {
        val response = FakeApiResponse(success = true, message = "Operation successful", data = "Sample Data")
        assertEquals(true, response.success)
        assertEquals("Operation successful", response.message)
        assertEquals("Sample Data", response.data)
    }

    @Test
    fun `should verify equality of two identical FakeApiResponse objects`() {
        val response1 = FakeApiResponse(success = true, message = "Operation successful", data = "Sample Data")
        val response2 = FakeApiResponse(success = true, message = "Operation successful", data = "Sample Data")
        assertEquals(response1, response2)
    }

    @Test
    fun `should verify inequality of two different FakeApiResponse objects`() {
        val response1 = FakeApiResponse(success = true, message = "Operation successful", data = "Sample Data")
        val response2 = FakeApiResponse(success = false, message = "Operation failed", data = "Different Data")
        assertNotEquals(response1, response2)
    }

    @Test
    fun `should handle null data in FakeApiResponse`() {
        val response = FakeApiResponse(success = true, message = "Operation successful", data = null)
        assertEquals(true, response.success)
        assertEquals("Operation successful", response.message)
    }
}