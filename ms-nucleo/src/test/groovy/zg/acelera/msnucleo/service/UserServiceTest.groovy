package zg.acelera.msnucleo.service

import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.repositories.user.UserRepository

class UserServiceTest extends Specification {
    UserRepository userRepository
    PasswordEncoder passwordEncoder
    UserService service

    def setup() {
        userRepository = Mock()
        passwordEncoder = Mock()
        service = new UserService(userRepository, passwordEncoder)
    }

    void "create hashes password and assigns role"() {
        given:
        UserCreateDTO dto = new UserCreateDTO(
                name: ' Ana ',
                email: ' ANA@EXAMPLE.COM ',
                password: 'secret1',
                description: ' Developer ')
        passwordEncoder.encode('secret1') >> '$2a$10$hashed-password'
        userRepository.findByEmailIgnoreCase('ana@example.com') >> Optional.empty()

        when:
        User result = service.create(dto, UserRole.CANDIDATE)

        then:
        1 * userRepository.save({
            User user -> user.name == 'Ana' &&
                    user.email == 'ana@example.com' &&
                    user.password == '$2a$10$hashed-password' &&
                    user.role == UserRole.CANDIDATE &&
                    user.description == 'Developer'
        }) >> { User user -> user }
        result.password == '$2a$10$hashed-password'
        result.role == UserRole.CANDIDATE
    }

    void "create uses candidate role by default"() {
        given:
        UserCreateDTO dto = new UserCreateDTO(
                name: 'Ana',
                email: 'ana@example.com',
                password: 'secret1',
                description: 'Developer')
        passwordEncoder.encode('secret1') >> '$2a$10$hashed-password'
        userRepository.findByEmailIgnoreCase('ana@example.com') >> Optional.empty()

        when:
        User result = service.create(dto)

        then:
        1 * userRepository.save(_) >> { User user -> user }
        result.role == UserRole.CANDIDATE
    }

    void "create rejects duplicate email"() {
        given:
        UserCreateDTO dto = new UserCreateDTO(
                name: 'Ana',
                email: ' USED@EXAMPLE.COM ',
                password: 'secret1',
                description: 'Developer')
        userRepository.findByEmailIgnoreCase('used@example.com') >>
                Optional.of(new User(id: UUID.randomUUID(), email: 'used@example.com'))

        when:
        service.create(dto, UserRole.COMPANY)

        then:
        thrown(IllegalArgumentException)
        0 * passwordEncoder.encode(_)
        0 * userRepository.save(_)
    }
}
