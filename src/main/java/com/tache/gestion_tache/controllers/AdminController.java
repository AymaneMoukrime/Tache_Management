package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.ProjectService;
import com.tache.gestion_tache.services.TeamService;
import com.tache.gestion_tache.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final TeamService teamService;

    // Get all users
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.findAll();
    }

    // Get a user by email
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    // Update user role (only for admin)
    @PostMapping("/user/{email}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable String email, @RequestParam String role) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();

        user.setUserRole(role.equalsIgnoreCase("ADMIN") ? UserRole.ADMIN : UserRole.NORMAL);
        userRepository.save(user);

        return ResponseEntity.ok("User role updated");
    }

    // Delete user by email
    @DeleteMapping("/user/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        userRepository.delete(user);

        return ResponseEntity.ok("User deleted");
    }

    // Get all projects
    @GetMapping("/projects")
    public List<ProjectResponse> getAllProjects() {
        return projectService.findAll();
    }

    // Update a project
    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody Project projectDetails) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setOwner(projectDetails.getOwner()); // Update owner if needed
        Project updatedProject = projectRepository.save(project);

        return ResponseEntity.ok(convertToProjectDto(updatedProject));
    }

    // Delete a project
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        projectRepository.deleteById(projectId);
        return ResponseEntity.ok("Project deleted");
    }

    // Get all teams
    @GetMapping("/teams")
    public List<TeamDto> getAllTeams() {
        return teamService.findAlladmin();
    }

    // Get a team by ID
    @GetMapping("/team/{teamId}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long teamId) {
        TeamDto teamDto = teamService.findById(teamId);
        return teamDto != null ? ResponseEntity.ok(teamDto) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // Delete a team by ID
    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long teamId) {
        teamRepository.deleteById(teamId);
        return ResponseEntity.ok("Team deleted");
    }

    // Helper method to convert Project to ProjectDto
    private ProjectResponse convertToProjectDto(Project project) {
        ProjectResponse projectDto = new ProjectResponse();
        projectDto.setId(project.getId());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        UserResponse userResponse = new UserResponse();
        userResponse.setId(project.getOwner().getId());
        userResponse.setName(project.getOwner().getName());
        userResponse.setEmail(project.getOwner().getEmail());
        projectDto.setOwner(userResponse); // Adjust according to your DTO structure
        return projectDto;
    }
}
