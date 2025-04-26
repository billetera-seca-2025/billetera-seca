package billetera_seca.repository

import billetera_seca.model.Movement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MovementRepository : JpaRepository<Movement, UUID>