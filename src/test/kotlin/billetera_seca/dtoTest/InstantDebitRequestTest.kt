package billetera_seca.dtoTest

import billetera_seca.dto.InstantDebitRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class InstantDebitRequestTest {

    @Test
    fun `should create InstantDebitRequest with correct values`() {
        val request = InstantDebitRequest(
            payerEmail = "payer@example.com",
            collectorEmail = "collector@example.com",
            amount = 50.0
        )

        assertEquals("payer@example.com", request.payerEmail)
        assertEquals("collector@example.com", request.collectorEmail)
        assertEquals(50.0, request.amount)
    }

    @Test
    fun `should verify equality of two identical InstantDebitRequest objects`() {
        val request1 = InstantDebitRequest(
            payerEmail = "payer@example.com",
            collectorEmail = "collector@example.com",
            amount = 50.0
        )
        val request2 = InstantDebitRequest(
            payerEmail = "payer@example.com",
            collectorEmail = "collector@example.com",
            amount = 50.0
        )

        assertEquals(request1, request2)
    }

    @Test
    fun `should verify inequality of two different InstantDebitRequest objects`() {
        val request1 = InstantDebitRequest(
            payerEmail = "payer@example.com",
            collectorEmail = "collector@example.com",
            amount = 50.0
        )
        val request2 = InstantDebitRequest(
            payerEmail = "payer@example.com",
            collectorEmail = "collector@example.com",
            amount = 100.0
        )

        assertNotEquals(request1, request2)
    }
}