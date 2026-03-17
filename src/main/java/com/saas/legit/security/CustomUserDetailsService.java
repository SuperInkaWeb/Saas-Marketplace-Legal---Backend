package com.saas.legit.security;

import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority(user.getRole().getNameRol().toUpperCase()));
        }

        Long tenantId = null;
        if (user.hasRole("LAWYER")) {
            tenantId = lawyerProfileRepository.findByUserIdUser(user.getIdUser())
                    .map(profile -> profile.getLawFirm() != null ? profile.getLawFirm().getIdLawFirm() : null)
                    .orElse(null);
        }

        return new CustomUserDetails(
                user.getIdUser(),
                user.getEmail(),
                user.getPasswordSecret(),
                authorities,
                tenantId
        );
    }

    public record CustomUserDetails(
            Long userId,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Long tenantId
    ) implements UserDetails {
        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
        @Override public String getPassword() { return password; }
        @Override public String getUsername() { return email; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
    }
}
