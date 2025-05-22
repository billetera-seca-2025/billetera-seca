package billetera_seca.model.dto


data class UpdateRequest(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val password: String
)