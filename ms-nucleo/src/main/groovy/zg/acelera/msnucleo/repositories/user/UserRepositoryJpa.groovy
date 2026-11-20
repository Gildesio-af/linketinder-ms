package zg.acelera.msnucleo.repositories.user

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.User

interface UserRepositoryJpa extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email)
}
