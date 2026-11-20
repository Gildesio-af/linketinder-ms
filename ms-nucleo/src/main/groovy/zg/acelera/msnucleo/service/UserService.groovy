package zg.acelera.msnucleo.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.repositories.user.UserRepository

@Service
class UserService {
    private final UserRepository userRepository
    private final PasswordEncoder passwordEncoder

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository
        this.passwordEncoder = passwordEncoder
    }

    @Transactional
    User create(UserCreateDTO dto, UserRole role = UserRole.CANDIDATE) {
        String email = dto.email.trim().toLowerCase()
        if (userRepository.findByEmailIgnoreCase(email).isPresent())
            throw new IllegalArgumentException("User with email '$email' already exists")

        User user = new User(
                name: dto.name.trim(),
                email: email,
                password: passwordEncoder.encode(dto.password),
                role: role,
                description: dto.description.trim())
        userRepository.save(user)
    }
}
