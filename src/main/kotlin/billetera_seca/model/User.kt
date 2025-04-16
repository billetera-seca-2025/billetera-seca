package billetera_seca.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(unique = true, nullable = false)
    val email: String,
    @Column(nullable = false)
    val password: String,
    @OneToOne
    @JoinColumn(name = "wallet_id")
    val wallet: Wallet,
    val createdAt: Date = Date()
)