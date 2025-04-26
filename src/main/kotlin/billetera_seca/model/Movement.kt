package billetera_seca.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "movements")
data class Movement(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    val wallet: Wallet, //Related to the wallet that made the movement

    @Column(nullable = false)
    val amount: Double,

    @Column(nullable = false)
    val type: String, // "income" or "outcome"

    val createdAt: Date = Date(),

    @Column(nullable = true)
    val relatedWalletId: UUID? = null  // Associated wallet ID for the movement (e.g., for P2P transactions)

)