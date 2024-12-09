package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/addProject")
    public Project createProject(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Project project) {
        return projectService.addproject(userDetails, project);
    }
    @PutMapping("/updateProject/{projectid}")
    public Project updateProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectid, @RequestBody Project updatedProject) {
        return projectService.updateProject(userDetails, projectid, updatedProject);
    }

    @DeleteMapping("/deleteProject/{projectid}")
    public ResponseEntity<String> deleteProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectid) {
        projectService.deleteProject(userDetails, projectid);
        return ResponseEntity.ok("Project deleted ");
    }
    @PostMapping("/addTeamToProject/{projectId}/team/{teamId}")
    public Project addTeamToProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectId, @PathVariable Long teamId) {
        return projectService.addTeamToProject(userDetails, projectId, teamId);
    }
    @PostMapping("/addTaskToProject/{projectId}/task/{taskId}")
    public Project addTaskToProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectId, @PathVariable Long taskId) {
        return projectService.addTaskToProject(userDetails, projectId, taskId);
    }
    @GetMapping("/getAllProjectsForUser")
    public List<Project> getAllProjectsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        return projectService.getAllProjectsForUser(userDetails);
    }

}

