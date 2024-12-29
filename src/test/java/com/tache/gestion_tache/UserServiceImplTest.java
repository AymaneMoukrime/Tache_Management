package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;  // Mock the user repository

    @InjectMocks
    private UserServiceImpl userService;  // Inject the mock into the service

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        // Initialize user with mock data
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("encryptedPassword");
        user.setUserRole(UserRole.NORMAL);

        // Mock UserDetails (AuthenticationPrincipal)
        userDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "encryptedPassword",
                List.of(new SimpleGrantedAuthority(UserRole.NORMAL.name()))
        );
    }

    @Test
    public void testFindByEmail_UserExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        ResponseEntity<?> response = userService.findByEmail("test@example.com");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserResponse userResponse = (UserResponse) response.getBody();
        assertNotNull(userResponse);
        assertEquals("test@example.com", userResponse.getEmail());
    }

    @Test
    public void testFindByEmail_UserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = userService.findByEmail("nonexistent@example.com");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testUpdateUser_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponse updatedUser = userService.updateUser(userDetails, "newemail@example.com", "New Name");

        // Then
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("New Name", updatedUser.getName());
    }

    @Test
    void testUpdateUser_EmailAlreadyInUse() {
        // Given
        User existingUser = new User();
        existingUser.setEmail("newemail@example.com");

        // Mock the repository methods to simulate the conditions for the exception
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));  // User being updated
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.of(existingUser));  // Email already in use

        // Act & Assert: Check if IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(userDetails, "newemail@example.com", "New Name");
        });

        // Assert: Check that the exception message matches the expected
        assertEquals("Email is already in use.", exception.getMessage());
    }
    @Test
    public void testUpdateUserImage_Success() throws IOException {
        // Given
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3, 4, 5});
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.updateUserImage(userDetails, mockFile);

        // Then
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, user.getImage());
    }

    @Test
    void testUpdateUserImage_UserNotFound() throws IOException {
        // Given
        // No need to mock file bytes since the exception is thrown before that logic
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);

        // Simulate user not found
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());  // Only mock user repository

        // When & Then: Expect RuntimeException to be thrown and assert the message
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserImage(userDetails, mockFile);  // This should throw the exception
        });

        // Assert the exception message matches
        assertEquals("User not found", exception.getMessage());  // Verify the exception message
    }


    @Test
    public void testGetUserProjects() {
        // Given
        List<Project> projects = new ArrayList<>();
        Project project = new Project();
        project.setId(1L);
        project.setName("Project 1");
        projects.add(project);
        user.setProjects(projects);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        List<ProjectResponse> projectResponses = userService.getUserProjects(userDetails);

        // Then
        assertNotNull(projectResponses);
        assertEquals(1, projectResponses.size());
        assertEquals("Project 1", projectResponses.get(0).getName());
    }
    @Test
    public void testGetUserTeams() {
        // Given
        List<Team> teams = new ArrayList<>();
        Team team = new Team();
        team.setId(1L);
        team.setName("Team 1");
        teams.add(team);
        Project project=new Project();
        team.setProject(project);
        user.setTeams(teams);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        List<TeamDto> teamDtos = userService.getUserTeams(userDetails);

        // Then
        assertNotNull(teamDtos);
        assertEquals(1, teamDtos.size());
        assertEquals("Team 1", teamDtos.get(0).getName());
    }

    @Test
    public void testUserDetailService_UserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetailsService userDetailsService = userService.userDetailService();
        UserDetails loadedUser = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(loadedUser);
        assertEquals("test@example.com", loadedUser.getUsername());
        assertEquals("encryptedPassword", loadedUser.getPassword());
        assertEquals(1, loadedUser.getAuthorities().size());
        assertEquals("NORMAL", loadedUser.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testUserDetailService_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UserDetailsService userDetailsService = userService.userDetailService();

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    public void testFindAll() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> userResponses = userService.findAll();

        assertNotNull(userResponses);
        assertEquals(1, userResponses.size());
        assertEquals("test@example.com", userResponses.get(0).getEmail());
    }

    @Test
    public void testFindalluser() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findalluser();

        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals("test@example.com", foundUsers.get(0).getEmail());
    }

    @Test
    public void testFindByName() {
        List<User> users = List.of(user);
        when(userRepository.findAllByNameStartingWith("Test")).thenReturn(users);

        List<UserResponse> userResponses = userService.findByName("Test");

        assertNotNull(userResponses);
        assertEquals(1, userResponses.size());
        assertEquals("test@example.com", userResponses.get(0).getEmail());
    }






}
