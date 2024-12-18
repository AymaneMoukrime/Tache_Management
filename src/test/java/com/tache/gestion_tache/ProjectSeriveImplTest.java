package com.tache.gestion_tache;


import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.entities.*;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.ProjetServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSender;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjetServiceImplTest {

    @InjectMocks
    private ProjetServiceImpl projetService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MailSender mailSender;

    private User user;
    private Project project;
    private Team team;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock a User object
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setUserRole(UserRole.NORMAL);

        // Mock a Project object
        project = new Project();
        project.setName("Test Project");
        project.setOwner(user);

        // Mock a Team object
        team = new Team();
        team.setId(1L);

        // Mock a Task object
        task = new Task();
        task.setId(1L);
    }

    // Test for adding a project
    @Test
    void testAddProject_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projetService.addproject(userDetails, project);

        assertNotNull(response);
        assertEquals("Test Project", response.getName());
    }

    @Test
    void testAddProject_ProjectNameEmpty() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        project.setName("");  // Empty name

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> projetService.addproject(userDetails, project));
    }

    @Test
    void testAddProject_EndDateBeforeCurrentDate() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        project.setEndDate(new Date(System.currentTimeMillis() - 86400000));  // 1 day before

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> projetService.addproject(userDetails, project));
    }

    // Test for updating a project
    @Test
    void testUpdateProject_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setName("Old Project");
        existingProject.setOwner(user);

        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");
        updatedProject.setEndDate(new Date(System.currentTimeMillis() + 86400000));  // 1 day later

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        ProjectResponse response = projetService.updateProject(userDetails, 1L, updatedProject);

        assertEquals("Updated Project", response.getName());
    }

    @Test
    void testUpdateProject_EndDateBeforeCurrentDate() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setName("Old Project");
        existingProject.setOwner(user);

        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");
        updatedProject.setEndDate(new Date(System.currentTimeMillis() - 86400000));  // 1 day before

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        assertThrows(RuntimeException.class, () -> projetService.updateProject(userDetails, 1L, updatedProject));
    }

    // Test for deleting a project
    @Test
    void testDeleteProject_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setOwner(user);

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        projetService.deleteProject(userDetails, 1L);

        verify(projectRepository, times(1)).delete(existingProject);
    }

    @Test
    void testDeleteProject_UserNotOwner() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        User anotherUser = new User();
        anotherUser.setEmail("anotheruser@example.com");

        Project existingProject = new Project();
        existingProject.setId(1L);
        existingProject.setOwner(anotherUser);  // Not the same owner

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        assertThrows(RuntimeException.class, () -> projetService.deleteProject(userDetails, 1L));
    }


    // Test for getting all projects for a user
    @Test
    void testGetAllProjectsForUser() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        Project existingProject = new Project();
        existingProject.setName("Test Project");
        existingProject.setOwner(user);

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findAllByUserAccess(user.getId())).thenReturn(List.of(existingProject));

        List<ProjectResponse> response = projetService.getAllProjectsForUser(userDetails);

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }
}