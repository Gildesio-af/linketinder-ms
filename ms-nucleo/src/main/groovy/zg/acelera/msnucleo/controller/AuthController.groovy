package zg.acelera.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.security.LoginRequestDTO
import zg.acelera.msnucleo.dto.security.LoginResponseDTO
import zg.acelera.msnucleo.infrastructure.security.TokenService
import zg.acelera.msnucleo.repositories.user.UserRepository

@RestController
@RequestMapping("/auth")
class AuthController {

    private final AuthenticationManager authenticationManager
    private final TokenService tokenService
    private final UserRepository userRepository

    AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager
        this.tokenService = tokenService
        this.userRepository = userRepository
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(request.email, request.password)
        authenticationManager.authenticate(credentials)

        User user = userRepository.findByEmailIgnoreCase(request.email).orElseThrow()
        String token = tokenService.generateToken(user)

        return ResponseEntity.ok(new LoginResponseDTO(token: token))
    }
}
