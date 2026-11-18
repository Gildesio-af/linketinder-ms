package zg.acelera.msnucleo.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.repositories.user.UserRepository

@Service
class UserService {
    private final UserRepository userRepository

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Transactional
    User create(UserCreateDTO dto) {
        String email = dto.email.trim().toLowerCase()
        if (userRepository.findByEmailIgnoreCase(email).isPresent())
            throw new IllegalArgumentException("User with email '$email' already exists")

        User user = new User(
                name: dto.name.trim(),
                email: email,
                password: dto.password,
                description: dto.description.trim())
        userRepository.save(user)
    }
}
