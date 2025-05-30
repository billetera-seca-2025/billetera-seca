package billetera_seca.model.dto

data class InstantDebitRequest(
    val receiverEmail: String,
    val bankName: String,
    val cbu: String,
    val amount: Double
)