package com.tache.gestion_tache;

import com.tache.gestion_tache.controllers.UserController;
import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setUserRole(UserRole.NORMAL); // Replace UserRole.USER with the appropriate role
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
    }

    @Test
    void testGetAllUsers() {
        List<UserResponse> userResponses = List.of(new UserResponse());
        when(userService.findAll()).thenReturn(userResponses);

        List<UserResponse> response = userController.appuser();

        assertEquals(userResponses, response);
        verify(userService, times(1)).findAll();
    }

    @Test
    void testGetUsersMail() {
        List<String> emails = List.of("test@example.com");
        when(userService.getAllMails()).thenReturn(emails);

        List<String> response = userController.getUsersMail();

        assertEquals(emails, response);
        verify(userService, times(1)).getAllMails();
    }

    @Test
    void testGetUserByName() {
        String name = "Test User";
        List<UserResponse> userResponses = List.of(new UserResponse());
        when(userService.findByName(anyString())).thenReturn(userResponses);

        List<UserResponse> response = userController.getuserByName(name);

        assertEquals(userResponses, response);
        verify(userService, times(1)).findByName(name);
    }


    @Test
    void testUpdateUser() {
        String email = "new@example.com";
        String name = "New Name";
        UserResponse userResponse = new UserResponse();
        when(userService.updateUser(any(UserDetails.class), anyString(), anyString())).thenReturn(userResponse);

        UserResponse response = userController.updateUser(userDetails, email, name);

        assertEquals(userResponse, response);
        verify(userService, times(1)).updateUser(userDetails, email, name);
    }

    @Test
    void testUpdateUserImage() throws IOException {
        ResponseEntity<String> responseEntity = ResponseEntity.ok("Image uploaded successfully!");
        doNothing().when(userService).updateUserImage(any(UserDetails.class), any(MultipartFile.class));

        ResponseEntity<String> response = userController.updateUserImage(userDetails, file);

        assertEquals(responseEntity, response);
        verify(userService, times(1)).updateUserImage(userDetails, file);
    }

    @Test
    void testGetUserImage() {
        byte[] imageBytes = new byte[]{1, 2, 3};
        mockUser.setImage(imageBytes);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        ResponseEntity<byte[]> response = userController.getUserImage(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(imageBytes, response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserProjects() {
        List<ProjectResponse> projectResponses = List.of(new ProjectResponse());
        when(userService.getUserProjects(any(UserDetails.class))).thenReturn(projectResponses);

        ResponseEntity<List<ProjectResponse>> response = userController.getUserProjects(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectResponses, response.getBody());
        verify(userService, times(1)).getUserProjects(userDetails);
    }

    @Test
    void testGetUserTeams() {
        List<TeamDto> teamDtos = List.of(new TeamDto());
        when(userService.getUserTeams(any(UserDetails.class))).thenReturn(teamDtos);

        ResponseEntity<List<TeamDto>> response = userController.getUserTeams(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teamDtos, response.getBody());
        verify(userService, times(1)).getUserTeams(userDetails);
    }

    @Test
    public void testGetImageMediaType_PNG() throws IOException {
        byte[] imageBytes = createImageBytes("png");
        MediaType mediaType = userController.getImageMediaType(imageBytes);
        assertEquals(MediaType.IMAGE_PNG, mediaType);
    }

    @Test
    public void testGetImageMediaType_InvalidImage() {
        byte[] imageBytes = new byte[0];
        MediaType mediaType = userController.getImageMediaType(imageBytes);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, mediaType);
    }

    private byte[] createImageBytes(String format) throws IOException {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }



}
