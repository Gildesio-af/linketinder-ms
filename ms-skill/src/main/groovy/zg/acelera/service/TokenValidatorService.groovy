package zg.acelera.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TokenValidatorService {

    private final Algorithm algorithm
    private static final String ISSUER = "linketinder-ms-nucleo"

    TokenValidatorService(@Value('${api.security.token.secret}') String secret) {
        this.algorithm = Algorithm.HMAC256(secret)
    }

    DecodedJWT validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
        } catch (JWTVerificationException e) {
            throw new IllegalArgumentException("Invalid or expired JWT token", e)
        }
    }
}
