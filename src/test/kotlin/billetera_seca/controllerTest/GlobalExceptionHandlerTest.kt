package billetera_seca.controllerTest

import billetera_seca.controller.GlobalExceptionHandler
import billetera_seca.exception.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@WebMvcTest(GlobalExceptionHandler::class)
class GlobalExceptionHandlerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return 400 for UserAlreadyExistsException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", UserAlreadyExistsException("User already exists"))
        }
            .andExpect {
                status().isBadRequest
                content().string("User already exists")
            }
    }

    @Test
    fun `should return 400 for InvalidEmailFormatException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", InvalidEmailFormatException("Invalid email format"))
        }
            .andExpect {
                status().isBadRequest
                content().string("Invalid email format")
            }
    }

    @Test
    fun `should return 400 for WeakPasswordException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", WeakPasswordException("Weak password"))
        }
            .andExpect {
                status().isBadRequest
                content().string("Weak password")
            }
    }

    @Test
    fun `should return 404 for UserNotFoundException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", UserNotFoundException("User not found"))
        }
            .andExpect {
                status().isNotFound
                content().string("User not found")
            }
    }

    @Test
    fun `should return 400 for InsufficientBalanceException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", InsufficientBalanceException("Insufficient balance"))
        }
            .andExpect {
                status().isBadRequest
                content().string("Insufficient balance")
            }
    }

    @Test
    fun `should return 400 for SelfTransferException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", SelfTransferException("Cannot transfer to self"))
        }
            .andExpect {
                status().isBadRequest
                content().string("Cannot transfer to self")
            }
    }

    @Test
    fun `should return 400 for NegativeOrZeroAmountException`() {
        mockMvc.get("/test") {
            requestAttr("javax.servlet.error.exception", NegativeOrZeroAmountException("Amount must be greater than zero"))
        }
            .andExpect {
                status().isBadRequest
                content().string("Amount must be greater than zero")
            }
    }
}