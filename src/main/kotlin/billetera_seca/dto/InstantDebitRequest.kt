package billetera_seca.dto

data class InstantDebitRequest(
    val payerEmail: String,
    val collectorEmail: String,
    val amount: Double
)