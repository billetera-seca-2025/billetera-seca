package billetera_seca.model.dto

data class FakeApiResponse<T>(val success: Boolean,
                           val message: String,
                           val data: T? = null
)
