package com.tache.gestion_tache.services;

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

import java.util.Date;
import java.util.List;
import java.util.Optional;


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
            return ResponseEntity.ok(taskRepository.findById(id));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
   }
   @Override
    public List<Task> findAllTasks(Long projectId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        Project project = projectService.getProject(projectId);
        if (project == null) {
            throw new RuntimeException("Project not found with id: " + projectId);
        }

        if (!user.getProjects().contains(project) ) {
            throw new RuntimeException("Unauthorized access to project tasks");
        }

        return taskRepository.findByProject(project);
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

        if (existingTask.getOwner().equals(user)) {
            existingTask.setTitre(updatedTask.getTitre());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setStatus(updatedTask.getStatus());
            return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(existingTask));

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to perform this action");
        // Update fields


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
