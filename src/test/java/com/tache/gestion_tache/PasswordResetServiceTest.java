package com.tache.gestion_tache;

import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    private User user;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock a User object
        user = new User();
        user.setEmail("testuser@example.com");
        user.setPasswordResetCode("encryptedCode");
        user.setPasswordResetCodeExpiry(new Date(System.currentTimeMillis() + 1000 * 60 * 15)); // 15-minute expiry
    }

    @Test
    void testSendResetCode_UserExists() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class)); // Mock sending email

        passwordResetService.sendResetCode("testuser@example.com");

        verify(userRepository, times(1)).save(user);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendResetCode_UserNotFound() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.empty());

        passwordResetService.sendResetCode("testuser@example.com");

        verify(userRepository, never()).save(any(User.class));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testValidateResetCode_ValidCode() {
        // Generate a code and encode it (as done in the actual method)
        String code = "123456"; // Plain code to test
        String encryptedCode = passwordEncoder.encode(code); // Encrypt it using BCrypt

        user.setPasswordResetCode(encryptedCode); // Set the encoded code
        user.setPasswordResetCodeExpiry(new Date(System.currentTimeMillis() + 1000 * 60 * 15)); // Set expiry date

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        // Validate the reset code by passing the plain code
        boolean isValid = passwordResetService.validateResetCode("testuser@example.com", code);

        assertTrue(isValid);
    }

    @Test
    void testValidateResetCode_InvalidCode() {
        user.setPasswordResetCode("encryptedCode"); // Mock encrypted code
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        boolean isValid = passwordResetService.validateResetCode("testuser@example.com", "wrongCode");

        assertFalse(isValid);
    }

    @Test
    void testValidateResetCode_ExpiredCode() {
        // Set an expired reset code expiry time
        user.setPasswordResetCodeExpiry(new Date(System.currentTimeMillis() - 1000 * 60 * 16)); // 16 minutes ago
        user.setPasswordResetCode("encryptedCode");
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        boolean isValid = passwordResetService.validateResetCode("testuser@example.com", "encryptedCode");

        assertFalse(isValid);
    }

    @Test
    void testValidateResetCode_UserNotFound() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.empty());

        boolean isValid = passwordResetService.validateResetCode("testuser@example.com", "encryptedCode");

        assertFalse(isValid);
    }
}