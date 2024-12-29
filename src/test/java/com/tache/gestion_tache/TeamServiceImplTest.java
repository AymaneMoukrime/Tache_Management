package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.services.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team team;
    private User user;
    private TeamDto teamDto;
    private Project project;

   @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mock entities
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");
        user.setUserRole(UserRole.NORMAL);

        team = new Team();
        team.setId(1L);
        team.setName("Team A");
        team.setDateCreation(java.sql.Date.valueOf("2022-01-01"));
        team.setUsers(Arrays.asList(user));
        project=new Project();
        team.setProject(project);



        // Create a DTO for the team
        teamDto = new TeamDto(team.getId(), team.getName(), team.getDateCreation(), 1L,Arrays.asList(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getDateInscription(), user.getUserRole().toString())));
    }

    @Test
    void testFindAll() {
        // Arrange
        when(teamRepository.findAll()).thenReturn(Arrays.asList(team));

        // Act
        List<Team> teams = teamService.findAll();

        // Assert
        assertNotNull(teams);
        assertEquals(1, teams.size());
        assertEquals(team.getName(), teams.get(0).getName());
    }

    @Test
    void testFindByName() {
        // Arrange
        when(teamRepository.findByNameAndUserIdAndProjectId("Team A", 1L, 1L)).thenReturn(Optional.of(team));

        // Act
        TeamDto foundTeam = teamService.findByName("Team A", 1L, 1L);

        // Assert
        assertNotNull(foundTeam);
        assertEquals(team.getName(), foundTeam.getName());
    }

    @Test
    void testFindByName_NotFound() {
        // Arrange
        when(teamRepository.findByNameAndUserIdAndProjectId("NonExistent", 1L, 1L)).thenReturn(Optional.empty());

        // Act
        TeamDto foundTeam = teamService.findByName("NonExistent", 1L, 1L);

        // Assert
        assertNull(foundTeam);
    }

    @Test
    void testFindByUser() {
        // Arrange
        when(teamRepository.findByUsers(user)).thenReturn(Arrays.asList(team));

        // Act
        List<TeamDto> teams = teamService.findByUser(user);

        // Assert
        assertNotNull(teams);
        assertEquals(1, teams.size());
        assertEquals(team.getName(), teams.get(0).getName());
    }

    @Test
    void testFindAllAdmin() {
        // Arrange
        when(teamRepository.findAll()).thenReturn(Arrays.asList(team));

        // Act
        List<TeamDto> teams = teamService.findAlladmin();

        // Assert
        assertNotNull(teams);
        assertEquals(1, teams.size());
        assertEquals(team.getName(), teams.get(0).getName());
    }

    @Test
    void testFindById() {
        // Arrange
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // Act
        TeamDto foundTeam = teamService.findById(1L);

        // Assert
        assertNotNull(foundTeam);
        assertEquals(team.getName(), foundTeam.getName());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(teamRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        TeamDto foundTeam = teamService.findById(1L);

        // Assert
        assertNull(foundTeam);
    }
}
