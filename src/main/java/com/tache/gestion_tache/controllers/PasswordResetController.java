package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.services.PasswordResetService;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/reset-password-request")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        passwordResetService.sendResetCode(email);
        return ResponseEntity.ok("Password reset code sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        if (passwordResetService.validateResetCode(email, code)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Hash the new password before saving
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            user.setPasswordResetCode(null); // Clear the reset code after successful password change
            user.setPasswordResetCodeExpiry(null); // Clear the expiry as well
            userRepository.save(user);

            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired code");
        }
    }
}
