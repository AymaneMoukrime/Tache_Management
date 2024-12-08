package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> appuser(){
        return userService.findAll();
    }
    @GetMapping("/UsersMail")
    public List<String> getUsersMail(){
        return userService.getAllMails();
    }
    @GetMapping("Userbyname/{name}")
    public List<UserResponse> getimByName(@PathVariable  String name){
        return userService.findByName(name);
    }

}
