package billetera_seca.modelTest

import billetera_seca.model.Transaction
import billetera_seca.model.TransactionType
import billetera_seca.model.Wallet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.*

class TransactionTests {

    @Test
    fun `should create Transaction with correct values`() {
        val wallet = Wallet()
        val transaction = Transaction(
            wallet = wallet,
            amount = 100.0,
            type = TransactionType.INCOME,
            relatedWalletId = UUID.randomUUID()
        )

        assertEquals(wallet, transaction.wallet)
        assertEquals(100.0, transaction.amount)
        assertEquals(TransactionType.INCOME, transaction.type)
    }

    @Test
    fun `should verify equality of two identical Transaction objects`() {
        val wallet = Wallet()
        val transaction1 = Transaction(
            wallet = wallet,
            amount = 100.0,
            type = TransactionType.INCOME
        )
        val transaction2 = Transaction(
            id = transaction1.id,
            wallet = wallet,
            amount = 100.0,
            type = TransactionType.INCOME
        )

        assertEquals(transaction1, transaction2)
    }

    @Test
    fun `should verify inequality of two different Transaction objects`() {
        val wallet = Wallet()
        val transaction1 = Transaction(
            wallet = wallet,
            amount = 100.0,
            type = TransactionType.INCOME
        )
        val transaction2 = Transaction(
            wallet = wallet,
            amount = 200.0,
            type = TransactionType.OUTCOME
        )

        assertNotEquals(transaction1, transaction2)
    }
}