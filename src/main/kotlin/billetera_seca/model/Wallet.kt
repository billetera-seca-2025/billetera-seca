package billetera_seca.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "wallets")
data class Wallet(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val balance:  Double = 0.0,
    val createdAt: Date = Date()
)