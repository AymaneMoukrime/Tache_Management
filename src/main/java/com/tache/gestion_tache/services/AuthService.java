package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;

public interface AuthService {

    User signUp(String email, String name, String password);
    boolean hasUserWithEmail(String email);
    void sendWelcomeEmail(UserResponse userDto);
}
