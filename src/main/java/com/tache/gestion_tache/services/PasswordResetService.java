package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void sendResetCode(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String code = generateResetCode();
            String encryptedCode = passwordEncoder.encode(code);
            Date expiryDate = new Date(System.currentTimeMillis() + 1000 * 60 * 15); // 15-minute expiry

            // Save the encrypted code and expiry date in the User entity
            user.setPasswordResetCode(encryptedCode);
            user.setPasswordResetCodeExpiry(expiryDate);
            userRepository.save(user);

            // Send the reset code via email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset Code");
            message.setText("Your password reset code is: " + code + "\nThis code will expire in 15 minutes.");
            mailSender.send(message);
        }
    }

    public boolean validateResetCode(String email, String code) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPasswordResetCode() != null && user.getPasswordResetCodeExpiry().after(new Date())) {
                if (passwordEncoder.matches(code, user.getPasswordResetCode())) {
                    return true; // Code is valid and not expired
                }
            }
        }
        return false; // Invalid code or expired
    }

    private String generateResetCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
