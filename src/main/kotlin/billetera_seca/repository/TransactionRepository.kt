package billetera_seca.repository

import billetera_seca.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TransactionRepository : JpaRepository<Transaction, UUID>