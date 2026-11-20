package zg.acelera.msnucleo.infrastructure.security

import com.auth0.jwt.interfaces.DecodedJWT
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.utils.exception.InvalidTokenException

class TokenServiceTest extends Specification {
    TokenService service

    def setup() {
        service = new TokenService('unit-test-secret-unit-test-secret')
        service.expiration = 1
    }

    void "generates token with subject and role claim"() {
        given:
        UUID id = UUID.randomUUID()
        User user = new User(
                id: id,
                name: 'Ana',
                email: 'ana@example.com',
                role: UserRole.CANDIDATE)

        when:
        String token = service.generateToken(user)
        DecodedJWT decoded = service.validateToken(token)

        then:
        decoded.subject == id.toString()
        decoded.getClaim('name').asString() == 'Ana'
        decoded.getClaim('email').asString() == 'ana@example.com'
        decoded.getClaim('role').asString() == 'ROLE_CANDIDATE'
    }

    void "rejects invalid token"() {
        when:
        service.validateToken('not-a-jwt')

        then:
        thrown(InvalidTokenException)
    }
}
