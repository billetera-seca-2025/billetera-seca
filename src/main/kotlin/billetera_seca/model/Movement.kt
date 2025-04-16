package billetera_seca.model

import java.util.UUID

data class Movement(
    val id: UUID = UUID.randomUUID(),
    val walletId: String,
    val amount: Double,
    // type can be income or outcome
    val type: String,
    val createdAt: String,
)
