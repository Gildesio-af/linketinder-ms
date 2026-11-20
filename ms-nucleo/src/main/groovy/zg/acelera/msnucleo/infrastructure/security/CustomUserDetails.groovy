package zg.acelera.msnucleo.infrastructure.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import zg.acelera.msnucleo.domain.User

class CustomUserDetails implements UserDetails{
    final User user

    CustomUserDetails(User user) {
        this.user = user
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return [new SimpleGrantedAuthority("ROLE_${user.role.name()}")]
    }

    @Override
    String getPassword() {
        return user.password
    }

    @Override
    String getUsername() {
        return user.email
    }

    @Override boolean isAccountNonExpired() { true }
    @Override boolean isAccountNonLocked() { true }
    @Override boolean isCredentialsNonExpired() { true }
    @Override boolean isEnabled() { true }
}
