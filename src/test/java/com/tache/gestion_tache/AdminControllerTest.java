package com.tache.gestion_tache;

import com.tache.gestion_tache.controllers.AdminController;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private AdminController adminController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUserRole(UserRole.ADMIN); // Replace UserRole.ADMIN with the appropriate role
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
    }

    @Test
    void testGetAllUsers() {
        List<UserResponse> userResponses = List.of(new UserResponse());
        when(userService.findAll()).thenReturn(userResponses);

        List<UserResponse> response = adminController.getAllUsers();

        assertEquals(userResponses, response);
        verify(userService, times(1)).findAll();
    }



    @Test
    void testUpdateUserRole() {
        String email = "test@example.com";
        String role = "ADMIN";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        ResponseEntity<String> response = adminController.updateUserRole(email, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testDeleteUser() {
        String email = "test@example.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        ResponseEntity<String> response = adminController.deleteUser(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testGetAllProjects() {
        List<ProjectResponse> projectResponses = List.of(new ProjectResponse());
        when(projectService.findAll()).thenReturn(projectResponses);

        List<ProjectResponse> response = adminController.getAllProjects();

        assertEquals(projectResponses, response);
        verify(projectService, times(1)).findAll();
    }

    @Test
    void testUpdateProject() {
        Long projectId = 1L;
        Project projectDetails = new Project();
        Project project = new Project();
        project.setId(projectId);
        project.setName("Old Name");
        project.setDescription("Old Description");

        // Set the owner for the project
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner Name");
        owner.setEmail("owner@example.com");
        owner.setUserRole(UserRole.NORMAL); // Replace UserRole.OWNER with the appropriate role
        project.setOwner(owner);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ResponseEntity<ProjectResponse> response = adminController.updateProject(projectId, projectDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectRepository, times(1)).save(project);
    }


    @Test
    void testDeleteProject() {
        Long projectId = 1L;

        ResponseEntity<String> response = adminController.deleteProject(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    void testGetAllTeams() {
        List<TeamDto> teamDtos = List.of(new TeamDto());
        when(teamService.findAlladmin()).thenReturn(teamDtos);

        List<TeamDto> response = adminController.getAllTeams();

        assertEquals(teamDtos, response);
        verify(teamService, times(1)).findAlladmin();
    }

    @Test
    void testGetTeamById() {
        Long teamId = 1L;
        TeamDto teamDto = new TeamDto();
        when(teamService.findById(anyLong())).thenReturn(teamDto);

        ResponseEntity<TeamDto> response = adminController.getTeamById(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teamDto, response.getBody());
        verify(teamService, times(1)).findById(teamId);
    }

    @Test
    void testDeleteTeam() {
        Long teamId = 1L;

        ResponseEntity<String> response = adminController.deleteTeam(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamRepository, times(1)).deleteById(teamId);
    }
}
