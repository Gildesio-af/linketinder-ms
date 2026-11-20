package zg.acelera.msnucleo.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.utils.exception.InvalidTokenException

import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService {

    @Value('${api.security.token.expiration_in_hours}')
    private int expiration
    private final Algorithm algorithm
    private static final String ISSUER = "linketinder-ms-nucleo"

    TokenService(@Value('${api.security.token.secret}') String secret) {
        this.algorithm = Algorithm.HMAC256(secret)
    }

    String generateToken(User user) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.id.toString())
                .withClaim("name", user.name)
                .withClaim("email", user.email)
                .withClaim("role", "ROLE_${user.role.name()}")
                .withExpiresAt(Instant.now().plus(expiration, ChronoUnit.HOURS))
                .sign(algorithm)
    }

    DecodedJWT validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException("Invalid token: ${e.message}")
        }
    }
}
