package billetera_seca.repository

import billetera_seca.model.Wallet
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WalletRepository : JpaRepository<Wallet, UUID>