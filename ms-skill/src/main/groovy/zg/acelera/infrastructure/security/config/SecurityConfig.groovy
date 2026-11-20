package zg.acelera.infrastructure.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import zg.acelera.infrastructure.security.JwtAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter

    SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .cors { }
                .csrf { it.disable() }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .exceptionHandling { exceptions ->
                    exceptions.authenticationEntryPoint { _, response, _ ->
                        response.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED)
                    }
                    exceptions.accessDeniedHandler { _, response, _ ->
                        response.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN)
                    }
                }
                .authorizeHttpRequests { auth ->
                    auth
                        .requestMatchers(HttpMethod.GET, "/skills/**").permitAll()
                        .anyRequest().authenticated()
                }
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter)
                .build()
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @org.springframework.beans.factory.annotation.Value('${app.cors.allowed-origins:http://localhost:3000}') String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins.split(',').collect { it.trim() }.findAll { it }
        configuration.allowedMethods = ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS']
        configuration.allowedHeaders = ['Authorization', 'Content-Type']
        configuration.allowCredentials = true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration('/**', configuration)
        return source
    }
}