package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.services.ProjectService;
import com.tache.gestion_tache.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;

    @GetMapping("/taskinfo/{projectid}/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long projectid,@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long id) {
        return taskService.findTaskById(projectid,userDetails,id);
    }

    @PostMapping("/createTask/{projectid}")
    public ResponseEntity<?> createTask(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid ,@RequestBody Task task) {

        return taskService.saveTask(projectid,userDetails,task);
    }

    @DeleteMapping("/deletetask/{id}")
    public ResponseEntity<?> deleteTask(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long id) {
        return taskService.deleteTask(userDetails,id);
    }
    @PutMapping("/updateTask/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task,@AuthenticationPrincipal UserDetails userDetails) {
       return taskService.updateTask(id,task,userDetails);
    }

    @PostMapping("/setstatus/{id}/{status}")
    public ResponseEntity<?> setStatus(@PathVariable Long id, @PathVariable String status,@AuthenticationPrincipal UserDetails userDetails) {
        return taskService.SetProgress(id,status,userDetails);
    }

    @GetMapping("/taskinfo/{projectid}")
    public List<Task> getTask(@PathVariable Long projectid, @AuthenticationPrincipal UserDetails userDetails){
        return  taskService.findAllTasks(projectid,userDetails);

    }

}
