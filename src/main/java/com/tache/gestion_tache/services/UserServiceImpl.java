package com.tache.gestion_tache.services;


import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;

import com.tache.gestion_tache.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailService() {
        return username -> {
            // Fetch the user from the database
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Map the user's role to a GrantedAuthority
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getUserRole().name()));

            // Return a UserDetails object with username, password, and authorities
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        };
    }



    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findalluser(){
        return userRepository.findAll();
    }

    @Override
    public List<UserResponse> findByName(String name) {
        return userRepository.findAllByNameStartingWith(name).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void updateUserImage(UserDetails userDetails, MultipartFile file) throws IOException {
        // Fetch the current authenticated user from the database using email from userDetails
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If the file is not empty, save the image
        if (file != null && !file.isEmpty()) {
            user.setImage(file.getBytes());  // Save image as byte array in the user entity
        }

        // Save the updated user back to the database
        userRepository.save(user);
    }
    @Override
    public ResponseEntity<?> findByEmail(String email){
        Optional<User> user =userRepository.findByEmail(email);
        if(user.isPresent()){
            return ResponseEntity.ok(mapToUserResponse(user.get()));
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
    @Override
    public List<String> getAllMails(){
        return userRepository.getEmailsUser();

    }


    @Override
    public UserResponse updateUser(UserDetails userDetails, String email, String name) {

        // Find the user by email (from authentication principal)
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() ->
                new IllegalArgumentException("User not found with email: " + userDetails.getUsername()));

        // Update email if necessary
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            // Check if the email is already in use by another user
            userRepository.findByEmail(email).ifPresent(existingUser -> {
                throw new IllegalArgumentException("Email is already in use.");
            });
            user.setEmail(email);
        }

        // Update name if provided
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }

        // Save the updated user to the database
        User updatedUser = userRepository.save(user);

        // Return the updated user response
        return mapToUserResponse(updatedUser);
    }

    @Override
    public List<ProjectResponse> getUserProjects(UserDetails userDetails) {
        // Find the user by email (from AuthenticationPrincipal)
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userDetails.getUsername()));

        // Retrieve the user's projects
        List<Project> projects = user.getProjects(); // This assumes the user entity has a 'projects' field

        // Map the projects to a response DTO (ProjectResponse)
        return projects.stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDto> getUserTeams(UserDetails userDetails) {
        // Find the user by email (from AuthenticationPrincipal)
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userDetails.getUsername()));

        // Retrieve the user's teams
        List<Team> teams = user.getTeams(); // This assumes the user entity has a 'teams' field

        // Map the teams to a response DTO (TeamResponse)
        return teams.stream()
                .map(this::mapToTeamResponse)
                .collect(Collectors.toList());
    }

    private TeamDto mapToTeamResponse(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName()
        );
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate()
        );
    }


    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateInscription(),
                user.getUserRole().toString()
        );
    }

}
