package zg.acelera.msnucleo.infrastructure.security

import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole

class CustomUserDetailsTest extends Specification {
    void "exposes credentials and role authority"() {
        given:
        User user = new User(
                email: 'ana@example.com',
                password: '$2a$10$hashed-password',
                role: UserRole.CANDIDATE)

        when:
        CustomUserDetails details = new CustomUserDetails(user)

        then:
        details.username == 'ana@example.com'
        details.password == '$2a$10$hashed-password'
        details.authorities*.authority == ['ROLE_CANDIDATE']
        details.accountNonExpired
        details.accountNonLocked
        details.credentialsNonExpired
        details.enabled
    }
}
