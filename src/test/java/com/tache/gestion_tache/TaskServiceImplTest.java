package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.TaskDto;
import com.tache.gestion_tache.entities.*;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.ProjectService;
import com.tache.gestion_tache.services.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private TaskServiceImpl taskService;

    private User user;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");
        user.setUserRole(UserRole.NORMAL);

        // Setup test project
        project = new Project();
        project.setId(1L);
        project.setOwner(user);

        // Setup test task
        task = new Task();
        task.setId(1L);
        task.setTitre("Test Task");
        task.setDescription("Test Description");
        task.setDateCreation(new Date());
        task.setDateDeadline(new Date());
        task.setPriorite(1L);
        task.setStatus(TaskStatus.TODO);
        task.setOwner(user);
        task.setUser(user);
        task.setProject(project);

        // Mock userDetails to return the correct email
        when(userDetails.getUsername()).thenReturn(user.getEmail());
    }

    @Test
    void testFindTaskById_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectService.getProject(1L)).thenReturn(project);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskService.findTaskById(1L, userDetails, 1L);

        assertEquals(200, response.getStatusCode().value()); // Updated here
        assertTrue(response.getBody() instanceof TaskDto);
        TaskDto taskDto = (TaskDto) response.getBody();
        assertEquals("Test Task", taskDto.getTitle());
    }

    @Test
    void testFindTaskById_TaskNotFound() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectService.getProject(1L)).thenReturn(project);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = taskService.findTaskById(1L, userDetails, 1L);

        assertEquals(404, response.getStatusCode().value()); // Updated here
        assertEquals("Task not found with id: 1", response.getBody());
    }

    @Test
    void testSaveTask_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectService.getProject(1L)).thenReturn(project);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        task.setDateDeadline(new Date());
        ResponseEntity<?> response = taskService.saveTask(1L, userDetails, task);


        assertEquals(201, response.getStatusCode().value()); // Updated here
        assertNotNull(response.getBody());
        assertEquals("Test Task", ((Task) response.getBody()).getTitre());
    }

    @Test
    void testSaveTask_Unauthorized() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(projectService.getProject(1L)).thenReturn(project);

        // Simulate unauthorized user
        User unauthorizedUser = new User();
        unauthorizedUser.setEmail("unauthorized@example.com");
        when(userRepository.findByEmail("unauthorized@example.com")).thenReturn(Optional.of(unauthorizedUser));
        when(userDetails.getUsername()).thenReturn("unauthorized@example.com");

        ResponseEntity<?> response = taskService.saveTask(1L, userDetails, task);

        assertEquals(401, response.getStatusCode().value()); // Updated here
        assertEquals("You are not authorized to perform this action", response.getBody());
    }

    @Test
    void testAssignTask_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail("assigned@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = taskService.assignTask(1L, userDetails, "assigned@example.com");

        assertEquals(200, response.getStatusCode().value()); // Updated here
        assertEquals("Task successfully assigned to user: assigned@example.com", response.getBody());
    }
    @Test
    void testAssignTask_TaskNotFound() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.assignTask(1L, userDetails, "assigned@example.com");
        });

        assertEquals("Task not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteTask_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskService.deleteTask(userDetails, 1L);

        assertEquals(200, response.getStatusCode().value()); // Updated here
        assertEquals("Task deleted successfully", response.getBody());
    }

    @Test
    void testDeleteTask_TaskNotFound() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = taskService.deleteTask(userDetails, 1L);

        assertEquals(404, response.getStatusCode().value()); // Updated here
        assertEquals("Task not found with id: 1", response.getBody());
    }

    @Test
    void testUpdateTask_Success() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        task.setTitre("Updated Task");
        task.setDateDeadline(new Date());

        ResponseEntity<?> response = taskService.updateTask(1L, task, userDetails);

        assertEquals(200, response.getStatusCode().value()); // Updated here
        assertEquals("Updated Task", ((Task) response.getBody()).getTitre());
    }

    @Test
    void testUpdateTask_Unauthorized() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Simulate unauthorized user
        User unauthorizedUser = new User();
        unauthorizedUser.setEmail("unauthorized@example.com");
        when(userRepository.findByEmail("unauthorized@example.com")).thenReturn(Optional.of(unauthorizedUser));
        when(userDetails.getUsername()).thenReturn("unauthorized@example.com");

        ResponseEntity<?> response = taskService.updateTask(1L, task, userDetails);

        assertEquals(403, response.getStatusCode().value()); // Updated here
        assertEquals("User is not authorized to update this task", response.getBody());
    }
}