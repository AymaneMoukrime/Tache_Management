package com.tache.gestion_tache.services;


import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;

import com.tache.gestion_tache.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

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



    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> findByName(String name) {
        return userRepository.findAllByNameStartingWith(name).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<String> getAllMails(){
        return userRepository.getEmailsUser();

    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateInscription(),
                user.getUserRole().toString()
        );
    }

}
