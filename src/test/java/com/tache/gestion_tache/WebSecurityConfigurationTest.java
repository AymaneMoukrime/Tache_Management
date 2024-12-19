package com.tache.gestion_tache;

import com.tache.gestion_tache.config.JwtAuthentificationFilter;
import com.tache.gestion_tache.config.WebSecurityConfiguration;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSecurityConfigurationTest {

    @Mock
    private JwtAuthentificationFilter jwtAuthentificationFilter;

    @Mock
    private UserService userService;

    public WebSecurityConfigurationTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPasswordEncoder() {
        WebSecurityConfiguration config = new WebSecurityConfiguration(jwtAuthentificationFilter, userService);
        PasswordEncoder passwordEncoder = config.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }


    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration mockConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager mockManager = mock(AuthenticationManager.class);

        when(mockConfig.getAuthenticationManager()).thenReturn(mockManager);

        WebSecurityConfiguration config = new WebSecurityConfiguration(jwtAuthentificationFilter, userService);
        AuthenticationManager authManager = config.authenticationManager(mockConfig);

        assertNotNull(authManager);
        assertEquals(mockManager, authManager);
    }


}
