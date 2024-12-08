package com.tache.gestion_tache.controllers;


import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/addProject")
    public Project createProject(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Project project) {
        return projectService.addproject(userDetails, project);

    }

}
