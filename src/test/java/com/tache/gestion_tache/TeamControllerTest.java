package com.tache.gestion_tache;



import com.tache.gestion_tache.controllers.TeamController;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TeamControllerTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamService teamService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TeamController teamController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
    }

    @Test
    void testCreateTeam() {
        Long projectId = 1L;
        Team team = new Team();
        Project project = new Project();
        project.setId(projectId);
        project.setOwner(mockUser);

        // Set the UserRole for the mockUser
        mockUser.setUserRole(UserRole.NORMAL); // Replace UserRole.OWNER with the appropriate role

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamRepository.findByNameAndProjectId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        ResponseEntity<?> response = teamController.createTeam(userDetails, projectId, team);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamRepository, times(1)).save(team);
    }



    @Test
    void testFindTeamByName() {
        Long projectId = 1L;
        String teamName = "Team A";
        TeamDto teamDto = new TeamDto();

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(teamService.findByName(anyString(), anyLong(), anyLong())).thenReturn(teamDto);

        ResponseEntity<?> response = teamController.findteambyname(userDetails, projectId, teamName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamService, times(1)).findByName(teamName, mockUser.getId(), projectId);
    }


    @Test
    void testAssignUserToTeam() {
        Long projectId = 1L;
        Long teamId = 1L;
        String email = "test@example.com";
        Project project = new Project();
        project.setId(projectId);
        project.setOwner(mockUser);
        Team team = new Team();
        team.setId(teamId);
        team.setProject(project);
        User addUser = new User();
        addUser.setId(2L);
        addUser.setEmail(email);
        addUser.setUserRole(UserRole.NORMAL);


        when(userDetails.getUsername()).thenReturn("owner@example.com");
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));
        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(addUser));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        ResponseEntity<?> response = teamController.assignUserToTeam(userDetails, projectId, teamId, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamRepository, times(1)).save(team);
    }


    @Test
    void testRemoveUserFromTeam() {
        Long projectId = 1L;
        Long teamId = 1L;
        String email = "test@example.com";
        Project project = new Project();
        project.setId(projectId);
        project.setOwner(mockUser);
        Team team = new Team();
        team.setId(teamId);
        team.setProject(project);
        User removeUser = new User();
        removeUser.setId(2L);
        removeUser.setEmail(email);
        team.getUsers().add(removeUser);

        // Set the UserRole for the mockUser
        mockUser.setUserRole(UserRole.NORMAL); // Replace UserRole.OWNER with the appropriate role

        when(userDetails.getUsername()).thenReturn("owner@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamRepository.findById(anyLong())).thenReturn(Optional.of(team));
        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(removeUser));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        ResponseEntity<?> response = teamController.removeUserFromTeam(userDetails, projectId, teamId, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamRepository, times(1)).save(team);
    }


    @Test
    void testDeleteTeam() {
        Long projectId = 1L;
        Long teamId = 1L;
        Project project = new Project();
        project.setId(projectId);
        project.setOwner(mockUser);
        Team team = new Team();
        team.setId(teamId);
        team.setProject(project);

        // Set the UserRole for the mockUser
        mockUser.setUserRole(UserRole.NORMAL); // Replace UserRole.OWNER with the appropriate role

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(teamRepository.findByIdAndProjectId(anyLong(), anyLong())).thenReturn(Optional.of(team));

        ResponseEntity<?> response = teamController.deleteTeam(userDetails, projectId, teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamRepository, times(1)).delete(team);
    }


    @Test
    void testFindTeamsByUser() {
        List<TeamDto> teamDtos = List.of(new TeamDto());

        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(teamService.findByUser(any(User.class))).thenReturn(teamDtos);

        ResponseEntity<?> response = teamController.findteamsByUser(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(teamService, times(2)).findByUser(mockUser);
    }


}
