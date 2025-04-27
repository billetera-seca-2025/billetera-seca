package ingisis.manager.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/users/register", "/users/login").permitAll()  // Permite acceso sin autenticación
                    .anyRequest().authenticated()  // Requiere autenticación para otros endpoints
            }
            .cors { it.configurationSource(corsConfigurationSource()) }  // Configuración CORS
            .csrf { it.disable() }  // Desactiva CSRF si es necesario
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()

        config.applyPermitDefaultValues()
        config.allowCredentials = true
        config.allowedOrigins = listOf("http://localhost:3000")
        config.allowedHeaders = listOf("authorization", "content-type", "*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")

        source.registerCorsConfiguration("/**", config)
        return source
    }
}
