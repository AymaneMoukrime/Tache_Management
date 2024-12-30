package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> appuser(){
        return userService.findAll();
    }
    @GetMapping("/UsersMail")
    public List<String> getUsersMail(){
        return userService.getAllMails();
    }
    @GetMapping("Userbyname/{name}")
    public List<UserResponse> getuserByName(@PathVariable  String name){
        return userService.findByName(name);
    }
    @GetMapping("UserbyEmail/{email}")
    public ResponseEntity<?> getuserByEmail(@PathVariable  String email){
        return userService.findByEmail(email);
    }

    @PostMapping("UpdateUser")
    public UserResponse updateUser(@AuthenticationPrincipal UserDetails userDetails,@RequestParam(required = false) String email,@RequestParam(required = false) String name){
        return userService.updateUser(userDetails, email, name);

    }

    @PostMapping("/updateUserImage")
    public ResponseEntity<String> updateUserImage(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            // Ensure the user is authenticated
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            // Call the service method to update the user's image
            userService.updateUserImage(userDetails, file);

            return ResponseEntity.ok("Image uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        // Fetch the current authenticated user from the database using email from userDetails
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If the user doesn't have an image, return a 404 response
        if (user.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        // Get the image bytes
        byte[] imageBytes = user.getImage();

        // Determine the correct MIME type based on the image format
        MediaType mediaType = getImageMediaType(imageBytes);

        // Return the image with the correct content type
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }

    // Helper method to determine the image MIME type
    public MediaType getImageMediaType(byte[] imageBytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            BufferedImage img = ImageIO.read(bis); // Try to read the image
            if (img == null) {
                return MediaType.APPLICATION_OCTET_STREAM; // Fallback if the image can't be read
            }

            // If it's a PNG image
            if (ImageIO.getImageWritersByFormatName("png").hasNext()) {
                return MediaType.IMAGE_PNG;
            }
            // If it's a JPEG image
            else if (ImageIO.getImageWritersByFormatName("jpg").hasNext() ||
                    ImageIO.getImageWritersByFormatName("jpeg").hasNext()) {
                return MediaType.IMAGE_JPEG;
            }
        } catch (IOException e) {
            // Handle exception (e.g., log it or return a default content type)
            e.printStackTrace();
        }
        return MediaType.APPLICATION_OCTET_STREAM; // Default fallback if no format is detected
    }


    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getUserProjects(@AuthenticationPrincipal UserDetails userDetails) {
        List<ProjectResponse> projects = userService.getUserProjects(userDetails);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/teams")
    public ResponseEntity<List<TeamDto>> getUserTeams(@AuthenticationPrincipal UserDetails userDetails) {
        List<TeamDto> teams = userService.getUserTeams(userDetails);
        return ResponseEntity.ok(teams);
    }



}
