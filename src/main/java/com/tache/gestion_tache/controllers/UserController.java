package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
