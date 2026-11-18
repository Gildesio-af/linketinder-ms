package zg.acelera.msnucleo.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.repositories.country.CountryRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class UserService {
    private final UserRepository userRepository
    private final CountryRepository countryRepository

    UserService(UserRepository userRepository, CountryRepository countryRepository) {
        this.userRepository = userRepository
        this.countryRepository = countryRepository
    }

    @Transactional
    User create(UserCreateDTO dto) {
        String email = dto.email.trim().toLowerCase()
        if (userRepository.findByEmailIgnoreCase(email).isPresent())
            throw new IllegalArgumentException("User with email '$email' already exists")

        Country country = countryRepository.findById(dto.countryId)
                .orElseThrow { new EntityNotFoundException("Country not found with id: ${dto.countryId}") }

        User user = new User(
                name: dto.name.trim(),
                email: email,
                password: dto.password,
                description: dto.description.trim(),
                country: country)
        userRepository.save(user)
    }
}
