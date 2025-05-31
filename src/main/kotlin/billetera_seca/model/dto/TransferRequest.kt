package billetera_seca.model.dto

data class TransferRequest(
    val senderEmail: String,
    val receiverEmail: String,
    val amount: Double
)
