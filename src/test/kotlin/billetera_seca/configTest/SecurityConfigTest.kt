package billetera_seca.configTest

import billetera_seca.config.SecurityConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.web.cors.CorsConfigurationSource
import org.mockito.Mockito.mock

@SpringBootTest(classes = [SecurityConfig::class, SecurityConfigTest.TestSecurityConfig::class])
class SecurityConfigTest {

    @Autowired
    private lateinit var securityConfig: SecurityConfig

    @TestConfiguration
    class TestSecurityConfig {
        @Bean
        fun httpSecurity(): HttpSecurity = mock(HttpSecurity::class.java)
    }
}