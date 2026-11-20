package zg.acelera.msnucleo.repositories.user

import zg.acelera.msnucleo.domain.User

interface UserRepository {
    Optional<User> findById(UUID id)
    Optional<User> findByEmailIgnoreCase(String email)
    User save(User user)
    boolean existsById(UUID id)
    void delete(UUID id)
}
