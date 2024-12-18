package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAdminAccount_AdminAlreadyExists() {
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.of(new User()));

        authService.createAdminAcount();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateAdminAccount_NoAdminExists() {
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.empty());

        authService.createAdminAcount();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("admin@example.com", savedUser.getEmail());
        assertEquals("admin", savedUser.getName());
        assertEquals(UserRole.ADMIN, savedUser.getUserRole());
        assertTrue(new BCryptPasswordEncoder().matches("admin", savedUser.getPassword()));
    }

    @Test
    void testSignUp() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword(new BCryptPasswordEncoder().encode("encodedPassword"));
        user.setDateInscription(new Date());
        user.setUserRole(UserRole.NORMAL);

        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authService.signUp("test@example.com", "Test User", "encodedPassword");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals(UserRole.NORMAL, result.getUserRole());
        assertTrue(new BCryptPasswordEncoder().matches("encodedPassword", result.getPassword()));
    }

    @Test
    void testHasUserWithEmail_Exists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        boolean exists = authService.hasUserWithEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testHasUserWithEmail_NotExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean exists = authService.hasUserWithEmail("test@example.com");

        assertFalse(exists);
    }

    @Test
    void testSendWelcomeEmail() {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("test@example.com");
        userResponse.setName("Test User");
        userResponse.setDateInscription(new Date());

        authService.sendWelcomeEmail(userResponse);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals("test@example.com", message.getTo()[0]);
        assertEquals("Bienvenue dans notre application !", message.getSubject());
        assertTrue(message.getText().contains("Cher(e) Test User,"));
    }
}
