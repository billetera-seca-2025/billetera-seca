package billetera_seca.modelTest

import billetera_seca.model.Transaction
import billetera_seca.model.TransactionType
import billetera_seca.model.Wallet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class WalletTest {

    @Test
    fun `should create Wallet with correct values`() {
        val wallet = Wallet(balance = 500.0)

        assertEquals(500.0, wallet.balance)
        assertTrue(wallet.transactions.isEmpty())
    }

    @Test
    fun `should add a transaction to Wallet`() {
        val wallet = Wallet()
        val transaction = Transaction(wallet = wallet, amount = 100.0, type = TransactionType.INCOME)

        val updatedTransactions = wallet.transactions + transaction
        val updatedWallet = wallet.copy(transactions = updatedTransactions)

        assertEquals(1, updatedWallet.transactions.size)
        assertEquals(transaction, updatedWallet.transactions[0])
    }
}