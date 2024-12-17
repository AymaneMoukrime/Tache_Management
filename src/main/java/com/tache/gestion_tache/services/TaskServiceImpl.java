package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.TaskDto;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.TaskStatus;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    @Override
    public ResponseEntity<?> findTaskById(Long projectid,UserDetails userDetails,Long id) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));
        Project project = projectService.getProject(projectid);
        if (project == null) {
            return ResponseEntity.badRequest().body("Project not found");
        }
        if(user.getProjects().contains(project) || project.getOwner().equals(user)){
            Task task = taskRepository.findById(id).orElse(null); // Return null if not found

            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Task not found with id: " + id);
            }
            TaskDto taskDto=new TaskDto(task.getId(),task.getTitre(),task.getDescription(),task.getDateCreation(),task.getDateDeadline()
            ,task.getPriorite(),task.getStatus(),task.getCouleur(),task.getOwner().getEmail(),task.getUser().getEmail());
            return ResponseEntity.ok(taskDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
   }
    @Override
    public List<TaskDto> findAllTasks(Long projectId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project project = projectService.getProject(projectId);
        if (project == null) {
            throw new RuntimeException("Project not found with id: " + projectId);
        }

        if (!user.getProjects().contains(project)) {
            throw new RuntimeException("Unauthorized access to project tasks");
        }

        // Fetch all tasks for the project
        List<Task> tasks = taskRepository.findByProject(project);

        // Map the list of tasks to a list of TaskDto objects

        return tasks.stream().map(task -> new TaskDto(
                task.getId(),
                task.getTitre(),
                task.getDescription(),
                task.getDateCreation(),
                task.getDateDeadline(),
                task.getPriorite(),
                task.getStatus(),
                task.getCouleur(),
                task.getOwner().getEmail(),
                task.getUser().getEmail()
        )).collect(Collectors.toList());
    }

   //Create A Task but only By the Owner of the Project
    @Override
    public ResponseEntity<?> saveTask(Long projectid,UserDetails userDetails, Task task) {
        // Find user by email
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project project = projectService.getProject(projectid);
        if (project == null) {
            return ResponseEntity.badRequest().body("Project not found");
        }
        if (project.getOwner().equals(user)) {
            task.setProject(project);
            if(project.getTasks().contains(task)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Task already exists");
            }
            // Associate user with the task
            task.setUser(user);
            if (task.getDateDeadline() != null && task.getDateDeadline().before(new Date())) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Deadline is before the current date");
            }
            task.setOwner(user);
            task.setStatus(TaskStatus.TODO);
            return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
    }

    //create a task and assign it to someone
    @Override
    public ResponseEntity<?> saveTaskWithAssign(Long projectId, UserDetails userDetails, Task task, String email) {
        // Find the user making the request
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        // Validate project
        Project project = projectService.getProject(projectId);
        if (project == null) {
            return ResponseEntity.badRequest().body("Project not found");
        }

        // Check authorization
        if (!project.getOwner().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
        }

        // Validate assigned user if email is provided
        User assignedUser = null;
        if (email != null && !email.isBlank()) {
            assignedUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
        }

        // Check for duplicate task
        if (project.getTasks().contains(task)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Task already exists");
        }

        // Validate deadline
        if (task.getDateDeadline() != null && task.getDateDeadline().before(new Date())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Deadline is before the current date");
        }

        // Set task details
        task.setProject(project);
        task.setUser(assignedUser != null ? assignedUser : user); // Default to creator if no assigned user
        task.setOwner(user);
        task.setStatus(TaskStatus.TODO);

        // Save task
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }

    //assign an already created task
    @Override
    public ResponseEntity<?> assignTask(Long taskId, UserDetails userDetails, String email) {
        // Find the user making the request
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        // Find the task to assign
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        // Check if the user is authorized (task owner or project owner)
        Project project = task.getProject();
        if (!task.getOwner().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the task owner can assign this task");
        }

        // Find the assigned user
        User assignedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Assigned user not found with email: " + email));

        // Assign the task
        task.setUser(assignedUser);

        // Save the updated task
        taskRepository.save(task);

        return ResponseEntity.ok("Task successfully assigned to user: " + email);
    }


    //Delete Task Only By the owner
    @Override
    public ResponseEntity<?> deleteTask(UserDetails userDetails,Long id) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));
         Optional<Task> task=taskRepository.findById(id);
        if(task.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found with id: " + id);
        }
        if (task.get().getOwner().equals(user)){
            taskRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Task deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You are not the owner of the Task: " + id);
    }

    @Override
    public ResponseEntity<?> updateTask(Long taskId, Task updatedTask,UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!existingTask.getOwner().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not authorized to update this task");
        }

        if (updatedTask.getStatus() != null) {
                existingTask.setStatus(updatedTask.getStatus());  // Set new status if valid
        }

        if (updatedTask.getDateDeadline() != null && updatedTask.getDateDeadline().before(new Date())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deadline cannot be in the past");
        }
        existingTask.setTitre(updatedTask.getTitre());
        existingTask.setDescription(updatedTask.getDescription());
        if(updatedTask.getCouleur()!=null && !updatedTask.getCouleur().isEmpty()){
        existingTask.setCouleur(updatedTask.getCouleur());}
        existingTask.setDateDeadline(updatedTask.getDateDeadline());

        // Save the updated task and return the response
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(existingTask));


    }



    @Override
    public ResponseEntity<?> SetProgress(Long taskId, String taskStatus,UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if(existingTask.getOwner().equals(user) || existingTask.getUser().equals(user)){
        existingTask.setStatus(TaskStatus.valueOf(taskStatus));
        return      ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(existingTask));}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
    }


}
