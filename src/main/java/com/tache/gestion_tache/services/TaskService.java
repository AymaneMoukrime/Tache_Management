package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Optional<Task> findTaskById(Long id);

    List<Task> findAllTasks();
    ResponseEntity<?> saveTask(UserDetails userDetails, Task task);
    void deleteTask(Long id);
    Task updateTask(Long taskId, Task updatedTask);
    Task SetProgress(Long taskId, String taskStatus);
}
