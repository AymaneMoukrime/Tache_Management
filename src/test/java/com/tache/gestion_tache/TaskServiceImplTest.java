package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.TaskDto;
import com.tache.gestion_tache.entities.*;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.ProjectService;
import com.tache.gestion_tache.services.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    private User mockUser;
    private Project mockProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUserRole(UserRole.NORMAL); // Replace UserRole.USER with the appropriate role

        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setName("Test Project");
        mockProject.setOwner(mockUser);

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(projectService.getProject(anyLong())).thenReturn(mockProject);
    }

    @Test
    void testFindTaskById() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitre("Test Task");
        task.setDescription("Test Description");
        task.setDateCreation(new Date());
        task.setDateDeadline(new Date());
        task.setPriorite(1L);
        task.setStatus(TaskStatus.TODO);
        task.setCouleur("Red");
        task.setOwner(mockUser);
        task.setUser(mockUser);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskService.findTaskById(1L, userDetails, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task.getId(), ((TaskDto) response.getBody()).getId());
        verify(taskRepository, times(1)).findById(taskId);
    }



    @Test
    void testSaveTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitre("Test Task");
        task.setDescription("Test Description");
        task.setDateCreation(new Date());
        task.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        task.setPriorite(1L);
        task.setStatus(TaskStatus.TODO);
        task.setCouleur("Red");
        task.setOwner(mockUser);
        task.setUser(mockUser);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        ResponseEntity<?> response = taskService.saveTask(1L, userDetails, task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task.getId(), ((Task) response.getBody()).getId());
        verify(taskRepository, times(1)).save(task);
    }




    @Test
    void testDeleteTask() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitre("Test Task");
        task.setDescription("Test Description");
        task.setDateCreation(new Date());
        task.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        task.setPriorite(1L);
        task.setStatus(TaskStatus.TODO);
        task.setCouleur("Red");
        task.setOwner(mockUser);
        task.setUser(mockUser);
        task.setProject(mockProject);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskService.deleteTask(userDetails, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void testUpdateTask() {
        Long taskId = 1L;
        Task updatedTask = new Task();
        updatedTask.setTitre("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        updatedTask.setCouleur("Blue");

        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitre("Test Task");
        existingTask.setDescription("Test Description");
        existingTask.setDateCreation(new Date());
        existingTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        existingTask.setPriorite(1L);
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setCouleur("Red");
        existingTask.setOwner(mockUser);
        existingTask.setUser(mockUser);
        existingTask.setProject(mockProject);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        ResponseEntity<?> response = taskService.updateTask(taskId, updatedTask, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void testSetProgress() {
        Long taskId = 1L;
        String taskStatus = "IN_PROGRESS";
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitre("Test Task");
        existingTask.setDescription("Test Description");
        existingTask.setDateCreation(new Date());
        existingTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        existingTask.setPriorite(1L);
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setCouleur("Red");
        existingTask.setOwner(mockUser);
        existingTask.setUser(mockUser);
        existingTask.setProject(mockProject);

    }
    @Test
    void testFindTaskById_TaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<?> response = taskService.findTaskById(1L, userDetails, taskId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found with id: " + taskId, response.getBody());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void testFindTaskById_Unauthorized() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setProject(mockProject);
        task.getProject().setOwner(new User());
        task.setOwner(new User());
        task.setUser(new User()); // Set the user field
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        ResponseEntity<?> response = taskService.findTaskById(1L, userDetails, taskId);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody());
    }


    @Test
    void testFindAllTasks_ProjectNotFound() {
        Long projectId = 1L;
        when(projectService.getProject(anyLong())).thenReturn(null);
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            taskService.findAllTasks(projectId, userDetails);
        });
        assertEquals("Project not found with id: " + projectId, exception.getMessage());
    }

    @Test
    void testFindAllTasks_Unauthorized() {
        Long projectId = 1L;
        Project unauthorizedProject = new Project();
        unauthorizedProject.setId(2L);
        when(projectService.getProject(anyLong())).thenReturn(unauthorizedProject);
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            taskService.findAllTasks(projectId, userDetails);
        });
        assertEquals("Unauthorized access to project tasks", exception.getMessage());
    }


    @Test
    void testUpdateTask_Unauthorized() {
        Long taskId = 1L;
        Task updatedTask = new Task();
        updatedTask.setTitre("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        updatedTask.setCouleur("Blue");
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitre("Test Task");
        existingTask.setDescription("Test Description");
        existingTask.setDateCreation(new Date());
        existingTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        existingTask.setPriorite(1L);
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setCouleur("Red");
        existingTask.setOwner(new User()); // Different owner
        existingTask.setUser(mockUser);
        existingTask.setProject(mockProject);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(existingTask));
        ResponseEntity<?> response = taskService.updateTask(taskId, updatedTask, userDetails);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User is not authorized to update this task", response.getBody());
    }

    @Test
    void testUpdateTask_DeadlineInPast() {
        Long taskId = 1L;
        Task updatedTask = new Task();
        updatedTask.setTitre("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setDateDeadline(new Date(System.currentTimeMillis() - 1000 * 1000)); // Past date
        updatedTask.setCouleur("Blue");
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitre("Test Task");
        existingTask.setDescription("Test Description");
        existingTask.setDateCreation(new Date());
        existingTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        existingTask.setPriorite(1L);
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setCouleur("Red");
        existingTask.setOwner(mockUser);
        existingTask.setUser(mockUser);
        existingTask.setProject(mockProject);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(existingTask));
        ResponseEntity<?> response = taskService.updateTask(taskId, updatedTask, userDetails);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Deadline cannot be in the past", response.getBody());
    }

    @Test
    void testSetProgress_Unauthorized() {
        Long taskId = 1L;
        String taskStatus = "IN_PROGRESS";
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitre("Test Task");
        existingTask.setDescription("Test Description");
        existingTask.setDateCreation(new Date());
        existingTask.setDateDeadline(new Date(System.currentTimeMillis() + 1000 * 1000));
        existingTask.setPriorite(1L);
        existingTask.setStatus(TaskStatus.TODO);
        existingTask.setCouleur("Red");
        existingTask.setOwner(new User()); // Different owner
        existingTask.setUser(new User()); // Different user
        existingTask.setProject(mockProject);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(existingTask));
        ResponseEntity<?> response = taskService.SetProgress(taskId, taskStatus, userDetails);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authorized to perform this action", response.getBody());
    }


}