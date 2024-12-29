package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.TaskDto;
import com.tache.gestion_tache.entities.*;
import com.tache.gestion_tache.repositories.ProjectRepository;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private ProjectRepository projectRepository;

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

    @Test
    void testSaveTaskWithAssign_UserNotFound() {
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Task task = new Task();
        task.setTitre("Test Task");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");
        });

        assertEquals("User not found with email: test@example.com", exception.getMessage());
    }

    @Test
    void testSaveTaskWithAssign_ProjectNotFound() {
        when(projectService.getProject(1L)).thenReturn(null);

        Task task = new Task();
        task.setTitre("Test Task");

        ResponseEntity<?> response = taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Project not found", response.getBody());
    }

    @Test
    void testSaveTaskWithAssign_Unauthorized() {
        mockProject.setOwner(new User()); // Ensure the user is not the owner

        Task task = new Task();
        task.setTitre("Test Task");

        ResponseEntity<?> response = taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("You are not authorized to perform this action", response.getBody());
    }

    @Test
    void testSaveTaskWithAssign_AssignedUserNotFound() {
        when(userRepository.findByEmail("assigneduser@example.com")).thenReturn(Optional.empty());

        Task task = new Task();
        task.setTitre("Test Task");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");
        });

        assertEquals("Assigned user not found", exception.getMessage());
    }

    @Test
    void testSaveTaskWithAssign_TaskAlreadyExists() {
        Task task = new Task();
        task.setTitre("Test Task");

        mockProject.getTasks().add(task); // Ensure the task already exists in the project

        ResponseEntity<?> response = taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Task already exists", response.getBody());
    }

    @Test
    void testSaveTaskWithAssign_DeadlineBeforeCurrentDate() {
        Task task = new Task();
        task.setTitre("Test Task");
        task.setDateDeadline(new Date(System.currentTimeMillis() - 1000)); // Set deadline before current date

        ResponseEntity<?> response = taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("Deadline is before the current date", response.getBody());
    }

    @Test
    void testSaveTaskWithAssign_Success() {
        Task task = new Task();
        task.setTitre("Test Task");

        User assignedUser = new User();
        assignedUser.setId(2L);
        assignedUser.setEmail("assigneduser@example.com");

        when(userRepository.findByEmail("assigneduser@example.com")).thenReturn(Optional.of(assignedUser));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

        ResponseEntity<?> response = taskService.saveTaskWithAssign(1L, userDetails, task, "assigneduser@example.com");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testAssignTask_UserNotFound() {
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.assignTask(1L, userDetails, "assigneduser@example.com");
        });

        assertEquals("User not found with email: test@example.com", exception.getMessage());
    }

    @Test
    void testAssignTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.assignTask(1L, userDetails, "assigneduser@example.com");
        });

        assertEquals("Task not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAssignTask_Unauthorized() {
        Task task = new Task();
        task.setId(1L);
        task.setOwner(new User()); // Ensure the user is not the owner
        task.setProject(mockProject);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<?> response = taskService.assignTask(1L, userDetails, "assigneduser@example.com");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Only the task owner can assign this task", response.getBody());
    }

    @Test
    void testAssignTask_AssignedUserNotFound() {
        Task task = new Task();
        task.setId(1L);
        task.setOwner(mockUser); // Ensure the user is the owner
        task.setProject(mockProject);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail("assigneduser@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.assignTask(1L, userDetails, "assigneduser@example.com");
        });

        assertEquals("Assigned user not found with email: assigneduser@example.com", exception.getMessage());
    }

    @Test
    void testAssignTask_Success() {
        Task task = new Task();
        task.setId(1L);
        task.setOwner(mockUser); // Ensure the user is the owner
        task.setProject(mockProject);

        User assignedUser = new User();
        assignedUser.setId(2L);
        assignedUser.setEmail("assigneduser@example.com");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findByEmail("assigneduser@example.com")).thenReturn(Optional.of(assignedUser));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

        ResponseEntity<?> response = taskService.assignTask(1L, userDetails, "assigneduser@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task successfully assigned to user: assigneduser@example.com", response.getBody());
    }

    @Test
    void testFindAllTasks_Success() {
        when(userRepository.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(projectService.getProject(1L)).thenReturn(mockProject);
        Task task = new Task();
        task.setId(1L);
        task.setTitre("Test Task");
        task.setDescription("Test Description");
        task.setDateCreation(new Date());
        task.setDateDeadline(new Date());
        task.setPriorite(1l);
        task.setStatus(TaskStatus.TODO);
        task.setCouleur("Red");
        task.setOwner(mockUser);
        task.setUser(mockUser);
        mockProject.getUsers().add(mockUser);
        mockUser.getProjects().add(mockProject);

        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByProject(mockProject)).thenReturn(tasks);

        List<TaskDto> taskDtos = taskService.findAllTasks(1L, userDetails);

        assertNotNull(taskDtos);
        assertFalse(taskDtos.isEmpty());
        assertEquals(1, taskDtos.size());
        assertEquals("Test Task", taskDtos.get(0).getTitle());
        assertEquals("Test Description", taskDtos.get(0).getDescription());
        assertEquals(1, taskDtos.get(0).getPriorite());
        assertEquals(TaskStatus.TODO, taskDtos.get(0).getStatus());
        assertEquals("Red", taskDtos.get(0).getCouleur());
        assertEquals("test@example.com", taskDtos.get(0).getTaskOwnerEmail());
        assertEquals("test@example.com", taskDtos.get(0).getTaskUserEmail());
    }








}