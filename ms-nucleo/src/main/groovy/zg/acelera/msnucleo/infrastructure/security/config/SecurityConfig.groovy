package zg.acelera.msnucleo.infrastructure.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import zg.acelera.msnucleo.infrastructure.security.JwtAuthenticationFilter

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
                .csrf { it.disable() }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authorizeHttpRequests { auth ->
                    auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/candidates", "/companies").permitAll()
                        .anyRequest().authenticated()
                }
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter)
                .build()
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.authenticationManager
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
