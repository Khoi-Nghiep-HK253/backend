package com.hcmut.divvy.security;

import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "User not found with username or email: " + usernameOrEmail)));

        String roleStr = user.getRole() != null ? user.getRole().name() : "USER";
        String roleName = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getHashPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName)));
    }
}
