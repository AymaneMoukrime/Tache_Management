package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.entities.Project;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ProjectService {
    ProjectResponse addproject(UserDetails userDetails, Project project);
    ProjectResponse updateProject(UserDetails userDetails, Long projectId, Project updatedProject);
    void deleteProject(UserDetails userDetails, Long projectId);
    Project addTeamToProject(UserDetails userDetails, Long projectId, Long teamId);
    Project addTaskToProject(UserDetails userDetails, Long projectId, Long taskId);
    List<ProjectResponse> getAllProjectsForUser(UserDetails userDetails);
    Project getProject(long id);
    List<ProjectResponse> findAll();
}