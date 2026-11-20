package zg.acelera.msnucleo.infrastructure.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.repositories.user.UserRepository

class CustomUserDetailsServiceTest extends Specification {
    UserRepository userRepository
    CustomUserDetailsService service

    def setup() {
        userRepository = Mock()
        service = new CustomUserDetailsService(userRepository)
    }

    void "loads user details by email"() {
        given:
        User user = new User(
                email: 'ana@example.com',
                password: '$2a$10$hashed-password',
                role: UserRole.CANDIDATE)
        userRepository.findByEmailIgnoreCase('ana@example.com') >> Optional.of(user)

        when:
        UserDetails details = service.loadUserByUsername('ana@example.com')

        then:
        details.username == 'ana@example.com'
        details.password == '$2a$10$hashed-password'
        details.authorities*.authority == ['ROLE_CANDIDATE']
    }

    void "throws when email is unknown"() {
        given:
        userRepository.findByEmailIgnoreCase('missing@example.com') >> Optional.empty()

        when:
        service.loadUserByUsername('missing@example.com')

        then:
        thrown(UsernameNotFoundException)
    }
}
