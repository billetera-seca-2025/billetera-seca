package billetera_seca.repository

import billetera_seca.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
}