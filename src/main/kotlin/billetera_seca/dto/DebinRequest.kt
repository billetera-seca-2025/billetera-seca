package billetera_seca.dto

data class DebinRequest(
    val payerEmail: String,
    val collectorEmail: String,
    val amount: Double
)