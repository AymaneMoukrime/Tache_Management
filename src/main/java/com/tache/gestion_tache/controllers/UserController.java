package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<User> appuser(){
        return userRepository.findAll();
    }
    @GetMapping("/UsersMail")
    public List<String> getUsersMail(){
        return userRepository.getEmails();
    }

}
