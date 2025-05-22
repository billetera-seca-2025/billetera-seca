package billetera_seca.dtoTest

import billetera_seca.model.dto.FakeApiResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class FakeApiResponseTest {

    @Test
    fun `should create FakeApiResponse with correct values`() {
        val response = FakeApiResponse(success = true)
        assertEquals(true, response.success)
    }

    @Test
    fun `should verify equality of two identical FakeApiResponse objects`() {
        val response1 = FakeApiResponse(success = true)
        val response2 = FakeApiResponse(success = true)
        assertEquals(response1, response2)
    }

    @Test
    fun `should verify inequality of two different FakeApiResponse objects`() {
        val response1 = FakeApiResponse(success = true)
        val response2 = FakeApiResponse(success = false)
        assertNotEquals(response1, response2)
    }
}