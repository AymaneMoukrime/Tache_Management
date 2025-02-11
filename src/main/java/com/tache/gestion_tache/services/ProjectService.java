package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.entities.Project;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<String> removeUserFromProject(UserDetails userDetails, Long projectId, Long userId);
    ProjectResponse getByid(UserDetails userDetails,Long id) throws IllegalAccessException;
    ResponseEntity<String> removeUserFromProjectBymail(UserDetails userDetails, Long projectId, String email);
    List<String> allUsersProject(UserDetails userDetails, Long projectId);
    ResponseEntity<String> addUserToProjectbyid(UserDetails userDetails, Long projectId, Long userId);
    ResponseEntity<String> addUserToProjectbymail(UserDetails userDetails, Long projectId, String Email);

}