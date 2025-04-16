package billetera_seca

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

// This class can be used to set up common test configurations or utilities
// that can be shared across multiple test classes.

@SpringBootTest
@ActiveProfiles("test")
@Transactional
abstract class BaseTest