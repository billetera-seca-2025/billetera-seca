package billetera_seca.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.web.reactive.function.client.WebClient

@TestConfiguration
class TestWebClientConfig {

    @Bean
    @Primary
    fun testWebClient(): WebClient {
        return WebClient.create("http://localhost:8081")  // Points to the test API service
    }
} 