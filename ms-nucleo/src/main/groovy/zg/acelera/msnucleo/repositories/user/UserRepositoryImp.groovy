package zg.acelera.msnucleo.repositories.user

import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.User

@Repository
class UserRepositoryImp implements UserRepository {
    private final UserRepositoryJpa userRepositoryJpa

    UserRepositoryImp(UserRepositoryJpa userRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa
    }

    @Override Optional<User> findById(UUID id) { userRepositoryJpa.findById(id) }
    @Override Optional<User> findByEmailIgnoreCase(String email) { userRepositoryJpa.findByEmailIgnoreCase(email) }
    @Override User save(User user) { userRepositoryJpa.save(user) }
    @Override boolean existsById(UUID id) { userRepositoryJpa.existsById(id) }
    @Override void delete(UUID id) { userRepositoryJpa.deleteById(id) }
}
