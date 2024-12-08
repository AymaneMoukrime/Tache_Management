package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.Project;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ProjectService {
    Project addproject(UserDetails userDetails, Project project);
    Project updateProject(UserDetails userDetails, Long projectId, Project updatedProject);
    void deleteProject(UserDetails userDetails, Long projectId);
    Project addTeamToProject(UserDetails userDetails, Long projectId, Long teamId);
    Project addTaskToProject(UserDetails userDetails, Long projectId, Long taskId);
    List<Project> getAllProjectsForUser(UserDetails userDetails);
    Project getProject(long id);
}