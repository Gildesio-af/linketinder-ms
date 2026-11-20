package zg.acelera.msnucleo.infrastructure.security

import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService

    JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7)
            try {
                DecodedJWT decoded = tokenService.validateToken(token)
                UUID userId = UUID.fromString(decoded.subject)
                String role = decoded.getClaim("role").asString()
                User user = new User(
                        id: userId,
                        email: decoded.getClaim("email").asString(),
                        role: UserRole.valueOf(role.replace("ROLE_", "")))
                CustomUserDetails principal = new CustomUserDetails(user)

                List<SimpleGrantedAuthority> authorities = [new SimpleGrantedAuthority(role)]
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities)

                SecurityContextHolder.context.authentication = auth
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }
}
