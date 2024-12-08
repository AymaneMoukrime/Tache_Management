package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailService();

    List<UserResponse> findAll();
    List<UserResponse> findByName( String name);
    List<String> getAllMails();
}
