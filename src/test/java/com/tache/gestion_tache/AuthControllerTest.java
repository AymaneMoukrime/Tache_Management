package com.tache.gestion_tache;

import com.tache.gestion_tache.controllers.AuthController;
import com.tache.gestion_tache.dto.AuthenticationRequest;
import com.tache.gestion_tache.dto.SignupRequest;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.AuthService;
import com.tache.gestion_tache.services.UserService;
import com.tache.gestion_tache.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignupUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setName("Test User");
        signupRequest.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.NORMAL);

        when(authService.hasUserWithEmail(anyString())).thenReturn(false);
        when(authService.signUp(anyString(), anyString(), anyString())).thenReturn(user);

        ResponseEntity<?> response = authController.signupUser(signupRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authService, times(1)).sendWelcomeEmail(any(UserResponse.class));
    }
    @Test
    void testLogin() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        UserDetailsService userDetailsService = mock(UserDetailsService.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.userDetailService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(jwtUtil.generateToken(any(UserDetails.class), anyLong())).thenReturn("jwtToken");

        ResponseEntity<?> response = authController.login(authenticationRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testSignupUser_UserAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");

        when(authService.hasUserWithEmail(anyString())).thenReturn(true);

        ResponseEntity<?> response = authController.signupUser(signupRequest);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }


    @Test
    void testLogin_BadCredentials() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@example.com");
        authenticationRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<?> response = authController.login(authenticationRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
