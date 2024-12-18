package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserDetailsService userDetailService();

    List<UserResponse> findAll();
    List<UserResponse> findByName( String name);
    List<String> getAllMails();
    List<User> findalluser();
    ResponseEntity<?> findByEmail(String email);
    UserResponse updateUser(UserDetails userDetails, String email, String name);
    List<ProjectResponse> getUserProjects(UserDetails userDetails);
    List<TeamDto> getUserTeams(UserDetails userDetails);
    void updateUserImage(UserDetails userDetails, MultipartFile file) throws IOException;
}
