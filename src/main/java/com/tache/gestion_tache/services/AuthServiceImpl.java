package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @PostConstruct
    public void createAdminAcount(){
        Optional<User> user = userRepository.findByUserRole(UserRole.ADMIN);
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setEmail("admin@example.com");
            newUser.setName("admin");
            newUser.setDateInscription(new Date());
            newUser.setPassword(new BCryptPasswordEncoder().encode("admin"));
            newUser.setUserRole(UserRole.ADMIN);
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
        user.setUserRole(UserRole.NORMAL);
        return userRepository.save(user);
    }
    @Override
    public boolean hasUserWithEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public void sendWelcomeEmail(UserResponse user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Bienvenue dans notre application !");
        message.setText("Cher(e) " + user.getName() + ",\n\n" +
                "Nous sommes ravis de vous accueillir parmi nous. Votre inscription a été effectuée avec succès ! le "+user.getDateInscription()+ "\n\n" +
                "Nous espérons que vous apprécierez pleinement les fonctionnalités de notre application. N'hésitez pas à nous contacter si vous avez des questions ou si nous pouvons vous aider de quelque manière que ce soit.\n\n" +
                "Cordialement,\n" +
                "L'équipe de notre application");
        mailSender.send(message);
    }
}
