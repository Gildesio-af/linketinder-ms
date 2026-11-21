package zg.acelera.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

class TokenValidatorServiceSpec extends Specification {

    private static final String SECRET = "unit-test-secret-key-at-least-32-chars"
    private static final String ISSUER = "linketinder-ms-nucleo"

    @Subject
    TokenValidatorService service = new TokenValidatorService(SECRET)

    def "validates a token signed with the configured secret"() {
        given:
        UUID userId = UUID.randomUUID()
        String token = createToken(SECRET, ISSUER, userId,
                Instant.now().plusSeconds(3600), "ROLE_CANDIDATE")

        when:
        DecodedJWT decoded = service.validateToken(token)

        then:
        decoded.subject == userId.toString()
        decoded.getClaim("role").asString() == "ROLE_CANDIDATE"
    }

    def "rejects a token signed with another secret"() {
        given:
        String token = createToken("another-secret-key-at-least-32-chars",
                ISSUER, UUID.randomUUID(), Instant.now().plusSeconds(3600), "ROLE_COMPANY")

        when:
        service.validateToken(token)

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "Invalid or expired JWT token"
    }

    def "rejects a token from another issuer"() {
        given:
        String token = createToken(SECRET, "another-issuer",
                UUID.randomUUID(), Instant.now().plusSeconds(3600), "ROLE_COMPANY")

        when:
        service.validateToken(token)

        then:
        thrown(IllegalArgumentException)
    }

    def "rejects an expired token"() {
        given:
        String token = createToken(SECRET, ISSUER,
                UUID.randomUUID(), Instant.now().minusSeconds(60), "ROLE_CANDIDATE")

        when:
        service.validateToken(token)

        then:
        thrown(IllegalArgumentException)
    }

    def "rejects malformed token content"() {
        when:
        service.validateToken("not-a-jwt")

        then:
        thrown(IllegalArgumentException)
    }

    private static String createToken(String secret, String issuer, UUID subject,
                                      Instant expiresAt, String role) {
        JWT.create()
                .withIssuer(issuer)
                .withSubject(subject.toString())
                .withClaim("role", role)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secret))
    }
}
