package billetera_seca.model.dto

import billetera_seca.model.TransactionType
import java.util.Date
import java.util.UUID

data class TransactionDTO(
    val amount: Double,
    val type: TransactionType,
    val createdAt: Date,
    val relatedWalletId: UUID?,
    val relatedBankName: String?
)
