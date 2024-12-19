package com.tache.gestion_tache;

import com.tache.gestion_tache.controllers.ProjectController;
import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProject() {
        Project project = new Project();
        ProjectResponse projectResponse = new ProjectResponse();
        when(projectService.addproject(any(UserDetails.class), any(Project.class))).thenReturn(projectResponse);

        ProjectResponse response = projectController.createProject(userDetails, project);

        assertEquals(projectResponse, response);
        verify(projectService, times(1)).addproject(userDetails, project);
    }

    @Test
    void testUpdateProject() {
        Long projectId = 1L;
        Project updatedProject = new Project();
        ProjectResponse projectResponse = new ProjectResponse();
        when(projectService.updateProject(any(UserDetails.class), anyLong(), any(Project.class))).thenReturn(projectResponse);

        ProjectResponse response = projectController.updateProject(userDetails, projectId, updatedProject);

        assertEquals(projectResponse, response);
        verify(projectService, times(1)).updateProject(userDetails, projectId, updatedProject);
    }

    @Test
    void testDeleteProject() {
        Long projectId = 1L;

        ResponseEntity<String> response = projectController.deleteProject(userDetails, projectId);

        assertEquals(ResponseEntity.ok("Project deleted "), response);
        verify(projectService, times(1)).deleteProject(userDetails, projectId);
    }



    @Test
    void testGetAllProjectsForUser() {
        List<ProjectResponse> projectResponses = List.of(new ProjectResponse());
        when(projectService.getAllProjectsForUser(any(UserDetails.class))).thenReturn(projectResponses);

        List<ProjectResponse> response = projectController.getAllProjectsForUser(userDetails);

        assertEquals(projectResponses, response);
        verify(projectService, times(1)).getAllProjectsForUser(userDetails);
    }
}
