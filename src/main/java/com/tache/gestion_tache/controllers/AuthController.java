package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.dto.AuthenticationRequest;
import com.tache.gestion_tache.dto.SignupRequest;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.dto.AuthenticationResponse;
import com.tache.gestion_tache.services.AuthService;
import com.tache.gestion_tache.services.UserService;
import com.tache.gestion_tache.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

//methode to create an account for the first time
    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest){
        if(signupRequest.getEmail()=="" || signupRequest.getName() ==""|| signupRequest.getPassword()==""){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("all the fields must be filled");
        }
        if (authService.hasUserWithEmail(signupRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already exists");}

        User createdUser = authService.signUp(signupRequest.getEmail(),signupRequest.getName(),signupRequest.getPassword());

        if (createdUser == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User could not be created");
        }
        UserResponse userResponse=new UserResponse(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getEmail(),
                createdUser.getDateInscription(),
                createdUser.getUserRole().name()
        );
        authService.sendWelcomeEmail(userResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
//methode to connect if you have an account already
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Return a 401 Unauthorized response if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
        }

        // Load user details
        final UserDetails userDetails = userService.userDetailService().loadUserByUsername(authenticationRequest.getEmail());

        // Fetch user from the repository
        Optional<User> optionalUser = userRepository.findByEmail(authenticationRequest.getEmail());
        if (optionalUser.isEmpty()) {
            // Return a 404 Not Found response if user is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Generate JWT token
        final String jwtToken = jwtUtil.generateToken(userDetails);

        // Build the authentication response
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwtToken);
        authenticationResponse.setUserId(optionalUser.get().getId());
        authenticationResponse.setUserRole(optionalUser.get().getUserRole());

        // Return a 200 OK response with the authentication details
        return ResponseEntity.ok(authenticationResponse);
    }

}
