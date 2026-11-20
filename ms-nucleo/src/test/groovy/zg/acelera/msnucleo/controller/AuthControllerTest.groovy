package zg.acelera.msnucleo.controller

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.security.LoginRequestDTO
import zg.acelera.msnucleo.dto.security.LoginResponseDTO
import zg.acelera.msnucleo.infrastructure.security.TokenService
import zg.acelera.msnucleo.repositories.user.UserRepository

class AuthControllerTest extends Specification {
    AuthenticationManager authenticationManager
    TokenService tokenService
    UserRepository userRepository
    AuthController controller

    def setup() {
        authenticationManager = Mock()
        tokenService = Mock()
        userRepository = Mock()
        controller = new AuthController(authenticationManager, tokenService, userRepository)
    }

    void "login authenticates user and returns token"() {
        given:
        LoginRequestDTO request = new LoginRequestDTO(
                email: 'ana@example.com',
                password: 'secret1')
        User user = new User(
                id: UUID.randomUUID(),
                name: 'Ana',
                email: 'ana@example.com',
                password: '$2a$10$hashed-password',
                role: UserRole.CANDIDATE)
        authenticationManager.authenticate({
            UsernamePasswordAuthenticationToken value ->
                    value.principal == 'ana@example.com' &&
                    value.credentials == 'secret1'
        }) >> Mock(org.springframework.security.core.Authentication)
        userRepository.findByEmailIgnoreCase('ana@example.com') >> Optional.of(user)
        tokenService.generateToken(user) >> 'signed-token'

        when:
        def response = controller.login(request)

        then:
        response.statusCode == HttpStatus.OK
        response.body instanceof LoginResponseDTO
        response.body.token == 'signed-token'
    }
}
