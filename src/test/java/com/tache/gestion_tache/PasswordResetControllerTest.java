package com.tache.gestion_tache;
import com.tache.gestion_tache.controllers.PasswordResetController;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class PasswordResetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(passwordResetController).build();
    }

    @Test
    void testRequestPasswordReset() throws Exception {
        // Given
        String email = "testuser@example.com";

        // When & Then: Test the reset-password-request endpoint
        mockMvc.perform(post("/api/auth/reset-password-request")
                        .param("Email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset code sent to your email."));

        // Verify that the service method was called
        verify(passwordResetService, times(1)).sendResetCode(email);
    }

    @Test
    void testResetPasswordWithValidCode() throws Exception {
        // Given
        String email = "testuser@example.com";
        String code = "123456";
        String newPassword = "newpassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword("oldpassword");
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(passwordResetService.validateResetCode(email, code)).thenReturn(true);

        // When & Then: Test the reset-password endpoint with valid code
        mockMvc.perform(post("/api/auth/reset-password")
                        .param("Email", email)
                        .param("code", code)
                        .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successful"));

        // Verify that the password is updated
        verify(userRepository, times(1)).save(user);
        assert new BCryptPasswordEncoder().matches(newPassword, user.getPassword());
    }

    @Test
    void testResetPasswordWithInvalidCode() throws Exception {
        // Given
        String email = "testuser@example.com";
        String code = "123456";
        String newPassword = "newpassword";
        when(passwordResetService.validateResetCode(email, code)).thenReturn(false);

        // When & Then: Test the reset-password endpoint with invalid code
        mockMvc.perform(post("/api/auth/reset-password")
                        .param("Email", email)
                        .param("code", code)
                        .param("newPassword", newPassword))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired code"));

        // Verify no save call is made
        verify(userRepository, times(0)).save(any(User.class));
    }
}
