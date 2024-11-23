package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @PostConstruct
    public void createAdminAcount(){
        Optional<User> user = userRepository.findByUserRole(UserRole.Admin);
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmail("admin@example.com");
            newUser.setName("admin");
            newUser.setDateInscription(new Date());
            newUser.setPassword(new BCryptPasswordEncoder().encode("admin"));
            newUser.setUserRole(UserRole.Admin);
            userRepository.save(newUser);
            //LOG
            System.out.println("Admin created");
        }else{
            System.out.println("Admin already exists");
        }

    }
    @Override
    public User signUp(String email, String name, String password){
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setDateInscription(new Date());
        user.setUserRole(UserRole.normal);
        return userRepository.save(user);
    }
    @Override
    public boolean hasUserWithEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
