package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.dto.TaskDto;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.services.ProjectService;
import com.tache.gestion_tache.services.TaskService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
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

    @PostMapping("/createTaskWithAssing/{projectid}")
    public  ResponseEntity<?> createTaskWithAssign(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectid, @RequestBody Task task, @RequestParam String Email) {
        return taskService.saveTaskWithAssign(projectid,userDetails,task,Email);
    }

    @PostMapping("/AssignTask/{taskid}")
    public ResponseEntity<?> assignTask(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long  taskid,@RequestParam String  Email) {
        return taskService.assignTask(taskid,userDetails,Email);
    }

    @DeleteMapping("/deletetask/{id}")
    public ResponseEntity<?> deleteTask(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long id) {
        return taskService.deleteTask(userDetails,id);
    }
    @PutMapping("/updateTask/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task,@AuthenticationPrincipal UserDetails userDetails) {
       return taskService.updateTask(id,task,userDetails);
    }

    @PutMapping("/setstatus/{id}/{status}")
    public ResponseEntity<?> setStatus(@PathVariable Long id, @PathVariable String status,@AuthenticationPrincipal UserDetails userDetails) {
        return taskService.SetProgress(id,status,userDetails);
    }

    @GetMapping("/taskinfo/{projectid}")
    public List<TaskDto> getTask(@PathVariable Long projectid, @AuthenticationPrincipal UserDetails userDetails){
        return  taskService.findAllTasks(projectid,userDetails);

    }

}
