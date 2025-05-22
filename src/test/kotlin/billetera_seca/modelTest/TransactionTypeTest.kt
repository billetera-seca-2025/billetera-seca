package billetera_seca.modelTest

import billetera_seca.model.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TransactionTypeTest {

    @Test
    fun `should verify TransactionType values`() {
        assertEquals("INCOME", TransactionType.INCOME.name)
        assertEquals("OUTCOME", TransactionType.OUTCOME.name)
    }
}