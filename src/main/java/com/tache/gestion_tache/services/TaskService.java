package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public interface TaskService {
    ResponseEntity<?> findTaskById(Long projectid,UserDetails userDetails,Long id);

    List<Task> findAllTasks(Long projectid,UserDetails userDetails);
    ResponseEntity<?> saveTask(Long projectid,UserDetails userDetails, Task task);
    ResponseEntity<?> deleteTask(UserDetails userDetails,Long id);
    ResponseEntity<?> updateTask(Long taskId, Task updatedTask,UserDetails userDetails);
    ResponseEntity<?> SetProgress(Long taskId, String taskStatus,UserDetails userDetails);
    ResponseEntity<?> saveTaskWithAssign(Long projectid,UserDetails userDetails, Task task,String email);
    ResponseEntity<?> assignTask(Long taskId, UserDetails userDetails, String email);
}
