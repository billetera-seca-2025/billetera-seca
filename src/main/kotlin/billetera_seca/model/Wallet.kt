package billetera_seca.model

import jakarta.persistence.*
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "wallets")
data class Wallet(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    var balance:  Double = 0.0,
    val createdAt: Date = Date(),

    @OneToMany(mappedBy = "wallet", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactions: List<Transaction> = mutableListOf()
)