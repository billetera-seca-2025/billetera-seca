package billetera_seca.configTest

import billetera_seca.config.WebClientConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest(classes = [WebClientConfig::class])
class WebClientConfigTest {

    @Autowired
    private lateinit var webClient: WebClient

}