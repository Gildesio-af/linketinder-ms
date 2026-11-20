package zg.acelera.msnucleo.infrastructure.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.repositories.user.UserRepository

@Service
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository

    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow { new UsernameNotFoundException("User not found with email: $email") }

        return new CustomUserDetails(user)
    }
}
