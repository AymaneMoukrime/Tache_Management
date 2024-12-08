package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.TaskStatus;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class TaskServiceImpl implements TaskService {
   @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Optional<Task> findTaskById(Long id) {
       return taskRepository.findById(id);
   }
    @Override
   public List<Task> findAllTasks() {
       return taskRepository.findAll();
   }
    @Override
    public ResponseEntity<?> saveTask(UserDetails userDetails, Task task) {
        // Find user by email
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));

        // Associate user with the task
        task.setUser(user);
        if(task.getDateDeadline().before(task.getDateCreation())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Deadline is before current date");
        }
        task.setStatus(TaskStatus.TODO);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }
    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    @Override
    public Task updateTask(Long taskId, Task updatedTask) {
        // Ensure the task exists before updating
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Update fields
        existingTask.setTitre(updatedTask.getTitre());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStatus(updatedTask.getStatus());

        return taskRepository.save(existingTask);
    }

    @Override
    public Task SetProgress(Long taskId, String taskStatus) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        existingTask.setStatus(TaskStatus.valueOf(taskStatus));
        return taskRepository.save(existingTask);
    }


}
