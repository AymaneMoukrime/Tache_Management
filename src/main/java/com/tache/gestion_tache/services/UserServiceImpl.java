package com.tache.gestion_tache.services;


import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailService() {
        return username -> {
            // Fetch the user from the database
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Map the user's role to a GrantedAuthority
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getUserRole().name()));

            // Return a UserDetails object with username, password, and authorities
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        };
    }

}
