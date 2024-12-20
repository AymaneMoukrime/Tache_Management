package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.entities.Project;
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
    public ProjectResponse createProject(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Project project) {
        return projectService.addproject(userDetails, project);
    }
    @PutMapping("/updateProject/{projectid}")
    public ProjectResponse updateProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectid, @RequestBody Project updatedProject) {
        return projectService.updateProject(userDetails, projectid, updatedProject);
    }

    @DeleteMapping("/deleteProject/{projectid}")
    public ResponseEntity<String> deleteProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectid) {
        projectService.deleteProject(userDetails, projectid);
        return ResponseEntity.ok("Project deleted ");
    }
   /* @PostMapping("/addTeamToProject/{projectId}/team/{teamId}")
    public Project addTeamToProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectId, @PathVariable Long teamId) {
        return projectService.addTeamToProject(userDetails, projectId, teamId);
    }
    @PostMapping("/addTaskToProject/{projectId}/task/{taskId}")
    public Project addTaskToProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectId, @PathVariable Long taskId) {
        return projectService.addTaskToProject(userDetails, projectId, taskId);
    }*/
    @PostMapping("/removeUserFromProject/{projectid}/user/{userid}")
    public ResponseEntity<String> removeUserFromProject(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid,@PathVariable Long userid) {
        return projectService.removeUserFromProject(userDetails, projectid, userid);
    }
    @GetMapping("/getAllProjectsForUser")
    public List<ProjectResponse> getAllProjectsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        return projectService.getAllProjectsForUser(userDetails);
    }

    @PostMapping("/removeUserFromProject/{projectid}/user")
    public ResponseEntity<String> removeUserFromProjectBymail(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid,@RequestParam String Email) {
        return projectService.removeUserFromProjectBymail(userDetails, projectid, Email);
    }

    @GetMapping("/UserOfProject/{projectid}")
    public List<String> gettAllUserOfProject(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid) {
        return  projectService.allUsersProject(userDetails, projectid);
    }

    @PostMapping("/addUserToProjectByid/{projectid}/user/{userid}")
    public ResponseEntity<String> addUserToProjectByid(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid,@PathVariable Long userid) {
        return projectService.addUserToProjectbyid(userDetails, projectid, userid);
    }
    @PostMapping("/addUserToProjectByemail/{projectid}/user")
    public ResponseEntity<String> addUserToProjectByid(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid,@RequestParam String Email) {
        return projectService.addUserToProjectbymail(userDetails, projectid, Email);
    }

    @GetMapping("getproject/{id}")
    public ProjectResponse getProject(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) throws IllegalAccessException {
        return projectService.getByid(userDetails, id);
    }

}

