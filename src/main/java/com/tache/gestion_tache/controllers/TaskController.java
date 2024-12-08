package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/taskinfo/{id}")
    public Optional<Task> getTask(@PathVariable Long id) {
        return taskService.findTaskById(id);
    }

    @PostMapping("/createTask")
    public ResponseEntity<?> createTask(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Task task) {
        return taskService.saveTask(userDetails,task);
    }

    @DeleteMapping("/deletetask/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
    @PutMapping("/updateTask/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
       return taskService.updateTask(id,task);
    }

    @PostMapping("/setstatus/{id}/{status}")
    public Task setStatus(@PathVariable Long id, @PathVariable String status) {
        return taskService.SetProgress(id,status);
    }

}
