package billetera_seca.dtoTest

import billetera_seca.model.dto.InstantDebitRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class InstantDebitRequestTest {

    @Test
    fun `should create InstantDebitRequest with correct values`() {
        val request = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "Example Bank",
            amount = 50.0
        )

        assertEquals("receiver@example.com", request.receiverEmail)
        assertEquals("Example Bank", request.bankName)
        assertEquals(50.0, request.amount)
    }

    @Test
    fun `should verify equality of two identical InstantDebitRequest objects`() {
        val request1 = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "Example Bank",
            amount = 50.0
        )
        val request2 = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "Example Bank",
            amount = 50.0
        )

        assertEquals(request1, request2)
    }

    @Test
    fun `should verify inequality of two different InstantDebitRequest objects`() {
        val request1 = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "Example Bank",
            amount = 50.0
        )
        val request2 = InstantDebitRequest(
            receiverEmail = "receiver@example.com",
            bankName = "Example Bank",
            amount = 100.0
        )

        assertNotEquals(request1, request2)
    }
}