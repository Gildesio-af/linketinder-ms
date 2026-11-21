package zg.acelera.infrastructure.security

import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.service.TokenValidatorService

class JwtAuthenticationFilterSpec extends Specification {

    TokenValidatorService tokenValidator = Mock()
    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()
    FilterChain filterChain = Mock()

    @Subject
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenValidator)

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "authenticates a valid bearer token"() {
        given:
        UUID userId = UUID.randomUUID()
        DecodedJWT decoded = Mock()
        Claim roleClaim = Mock()
        request.getHeader("Authorization") >> "Bearer valid-token"
        tokenValidator.validateToken("valid-token") >> decoded
        decoded.subject >> userId.toString()
        decoded.getClaim("role") >> roleClaim
        roleClaim.asString() >> "ROLE_CANDIDATE"

        when:
        filter.doFilter(request, response, filterChain)

        then:
        SecurityContextHolder.context.authentication.principal == userId
        SecurityContextHolder.context.authentication.authorities*.authority == ["ROLE_CANDIDATE"]
        1 * filterChain.doFilter(request, response)
    }

    def "clears authentication when the token is invalid"() {
        given:
        request.getHeader("Authorization") >> "Bearer invalid-token"
        tokenValidator.validateToken("invalid-token") >>
                { throw new IllegalArgumentException("Invalid or expired JWT token") }

        when:
        filter.doFilter(request, response, filterChain)

        then:
        SecurityContextHolder.context.authentication == null
        1 * filterChain.doFilter(request, response)
    }

    def "clears authentication when the subject is not a UUID"() {
        given:
        DecodedJWT decoded = Mock()
        Claim roleClaim = Mock()
        request.getHeader("Authorization") >> "Bearer malformed-token"
        tokenValidator.validateToken("malformed-token") >> decoded
        decoded.subject >> "not-a-uuid"
        decoded.getClaim("role") >> roleClaim
        roleClaim.asString() >> "ROLE_CANDIDATE"

        when:
        filter.doFilter(request, response, filterChain)

        then:
        SecurityContextHolder.context.authentication == null
        1 * filterChain.doFilter(request, response)
    }

    def "clears authentication when the role is not supported"() {
        given:
        DecodedJWT decoded = Mock()
        Claim roleClaim = Mock()
        request.getHeader("Authorization") >> "Bearer wrong-role-token"
        tokenValidator.validateToken("wrong-role-token") >> decoded
        decoded.subject >> UUID.randomUUID().toString()
        decoded.getClaim("role") >> roleClaim
        roleClaim.asString() >> "ROLE_ADMIN"

        when:
        filter.doFilter(request, response, filterChain)

        then:
        SecurityContextHolder.context.authentication == null
        1 * filterChain.doFilter(request, response)
    }

    def "continues anonymously without an authorization header"() {
        given:
        request.getHeader("Authorization") >> null

        when:
        filter.doFilter(request, response, filterChain)

        then:
        SecurityContextHolder.context.authentication == null
        0 * tokenValidator._
        1 * filterChain.doFilter(request, response)
    }
}
