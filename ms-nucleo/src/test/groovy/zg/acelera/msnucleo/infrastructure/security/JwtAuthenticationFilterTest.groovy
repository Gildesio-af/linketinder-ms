package zg.acelera.msnucleo.infrastructure.security

import jakarta.servlet.FilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole

class JwtAuthenticationFilterTest extends Specification {
    TokenService tokenService
    JwtAuthenticationFilter filter

    def setup() {
        tokenService = new TokenService('unit-test-secret-unit-test-secret')
        tokenService.expiration = 1
        filter = new JwtAuthenticationFilter(tokenService)
        SecurityContextHolder.clearContext()
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    void "sets authentication from valid bearer token"() {
        given:
        UUID id = UUID.randomUUID()
        User user = new User(
                id: id,
                name: 'Ana',
                email: 'ana@example.com',
                role: UserRole.CANDIDATE)
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.addHeader('Authorization', "Bearer ${tokenService.generateToken(user)}")
        MockHttpServletResponse response = new MockHttpServletResponse()
        FilterChain chain = Mock()

        when:
        filter.doFilter(request, response, chain)

        then:
        1 * chain.doFilter(request, response)
        SecurityContextHolder.context.authentication.principal instanceof CustomUserDetails
        SecurityContextHolder.context.authentication.principal.user.id == id
        SecurityContextHolder.context.authentication.authorities*.authority == ['ROLE_CANDIDATE']
    }

    void "clears authentication for invalid bearer token"() {
        given:
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.addHeader('Authorization', 'Bearer invalid-token')
        MockHttpServletResponse response = new MockHttpServletResponse()
        FilterChain chain = Mock()

        when:
        filter.doFilter(request, response, chain)

        then:
        1 * chain.doFilter(request, response)
        SecurityContextHolder.context.authentication == null
    }
}
