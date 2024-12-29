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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
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

    @Test
    void testGetByid_UserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.getByid(userDetails, 1L);
        });

        assertEquals("User not found with email: testuser@example.com", exception.getMessage());
    }

    @Test
    void testGetByid_ProjectNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.getByid(userDetails, 1L);
        });

        assertEquals("Project not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetByid_UserCannotAccessProject() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        project.getUsers().clear(); // Ensure the user is not part of the project

        Exception exception = assertThrows(IllegalAccessException.class, () -> {
            projetService.getByid(userDetails, 1L);
        });

        assertEquals("cannot access this", exception.getMessage());
    }

    @Test
    void testGetByid_Success() throws IllegalAccessException {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        project.getUsers().add(user); // Ensure the user is part of the project

        ProjectResponse response = projetService.getByid(userDetails, 1L);

        assertNotNull(response);
        assertEquals("Test Project", response.getName());
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

    @Test
    void testAddTaskToProject_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        Project project = new Project();
        project.setOwner(new User());  // Different owner

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () -> projetService.addTaskToProject(userDetails, 1L, 1L));
    }
    @Test
    void testAddUserToProjectById_UserAlreadyExists() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);
        project.setOwner(user);
        project.getUsers().add(user);
        user.getProjects().add(project);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> projetService.addUserToProjectbyid(userDetails, 1L, 1L));
    }
    @Test
    void testAddTeamToProject_TeamAlreadyExists() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);
        project.setOwner(user);

        Team team = new Team();
        team.setId(1L);
        project.getTeams().add(team);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        assertThrows(RuntimeException.class, () -> projetService.addTeamToProject(userDetails, 1L, 1L));
    }

    @Test
    void testGetByid_UserDoesNotHaveAccess() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(IllegalAccessException.class, () -> projetService.getByid(userDetails, 1L));
    }
  /*  @Test
    void testAddUserToProjectByEmail_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);
        project.setOwner(new User());// Different owner

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () -> projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com"));
    }*/
    @Test
    void testRemoveUserFromProjectByEmail_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);
        project.setOwner(new User());  // Different owner

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () -> projetService.removeUserFromProjectBymail(userDetails, 1L, "user@example.com"));
    }
    @Test
    void testAddTeamToProject_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);
        project.setOwner(new User());  // Different owner

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () -> projetService.addTeamToProject(userDetails, 1L, 1L));
    }
    @Test
    void testRemoveUserFromProject_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        Project project = new Project();
        project.setId(1L);
        project.setOwner(new User());  // Different owner

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(RuntimeException.class, () -> projetService.removeUserFromProject(userDetails, 1L, 1L));
    }

    @Test
    void testRemoveUserFromProject_UserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.removeUserFromProject(userDetails, 1L, 1L);
        });

        assertEquals("User not found with email: testuser@example.com", exception.getMessage());
    }

    @Test
    void testRemoveUserFromProject_ProjectNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.removeUserFromProject(userDetails, 1L, 1L);
        });

        assertEquals("Project not found", exception.getMessage());
    }


    @Test
    void testRemoveUserFromProject_ExistenUserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.removeUserFromProject(userDetails, 1L, 1L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testRemoveUserFromProject_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        project.setOwner(user); // Ensure the user is the owner
        project.getUsers().add(user); // Ensure the user is part of the project

        ResponseEntity<String> response = projetService.removeUserFromProject(userDetails, 1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed From project", response.getBody());
    }

    @Test
    void testRemoveUserFromProjectBymail_UserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.removeUserFromProjectBymail(userDetails, 1L, "testuser@example.com");
        });

        assertEquals("User not found with email: testuser@example.com", exception.getMessage());
    }

    @Test
    void testRemoveUserFromProjectBymail_ProjectNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.removeUserFromProjectBymail(userDetails, 1L, "testuser@example.com");
        });

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testRemoveUserFromProjectBymail_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        project.setOwner(new User()); // Ensure the user is not the owner

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.removeUserFromProjectBymail(userDetails, 1L, "testuser@example.com");
        });

        assertEquals("You do not have permission ", exception.getMessage());
    }

    @Test
    void testRemoveUserFromProjectBymail_ExistenUserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.removeUserFromProjectBymail(userDetails, 1L, "testuser@example.com");
        });

        assertEquals("User not found with email: "+userDetails.getUsername(), exception.getMessage());
    }

    @Test
    void testRemoveUserFromProjectBymail_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        project.setOwner(user); // Ensure the user is the owner
        project.getUsers().add(user); // Ensure the user is part of the project

        ResponseEntity<String> response = projetService.removeUserFromProjectBymail(userDetails, 1L, "testuser@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User removed from project", response.getBody());
    }

    @Test
    void testAllUsersProject_UserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.allUsersProject(userDetails, 1L);
        });

        assertEquals("User not found with email: testuser@example.com", exception.getMessage());
    }

    @Test
    void testAllUsersProject_ProjectNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.allUsersProject(userDetails, 1L);
        });

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testAllUsersProject_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        project.getUsers().clear(); // Ensure the user is not part of the project

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.allUsersProject(userDetails, 1L);
        });

        assertEquals("You do not have permission ", exception.getMessage());
    }


    @Test
    void testAddUserToProjectbymail_UserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com");
        });

        assertEquals("User not found with email: testuser@example.com", exception.getMessage());
    }

    @Test
    void testAddUserToProjectbymail_ProjectNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com");
        });

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testAddUserToProjectbymail_NoPermission() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        project.setOwner(new User()); // Ensure the user is not the owner

        ResponseEntity<String> response = projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com");

        assertEquals("You do not have permission ", response.getBody());
    }

    @Test
    void testAddUserToProjectbymail_ExistenUserNotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com");
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddUserToProjectbymail_UserAlreadyExists() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.of(user));

        project.setOwner(user); // Ensure the user is the owner
        user.getProjects().add(project); // Ensure the user is already part of the project

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com");
        });

        assertEquals("User already exists in this project", exception.getMessage());
    }

    @Test
    void testAddUserToProjectbymail_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser@example.com");
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.of(user));

        project.setOwner(user); // Ensure the user is the owner

        ResponseEntity<String> response = projetService.addUserToProjectbymail(userDetails, 1L, "newuser@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user added", response.getBody());
    }

    @Test
    void testGetProject_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            projetService.getProject(1L);
        });

        assertEquals("Project not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Project result = projetService.getProject(1L);

        assertNotNull(result);
        assertEquals("Test Project", result.getName());
    }

    @Test
    void testFindAll_Success() {
        List<Project> projects = Arrays.asList(project);
        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponse> responses = projetService.findAll();

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals("Test Project", responses.get(0).getName());
    }




}