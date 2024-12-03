package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.TaskRepository;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.TeamServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamServiceImpl teamService;


    @PostMapping("/create_team")
    public ResponseEntity<?> createTeam(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Team team) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        team.setDateCreation(new Date());
        team.getUsers().add(user);
        Team savedTeam = teamRepository.save(team);

        // Convert to DTO
        TeamDto teamDTO = convertToDTO(savedTeam);
        return ResponseEntity.ok(teamDTO);
    }

    @PostMapping("/teamname/{teamname}")
    public ResponseEntity<?> findteambyname(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String teamname) {
        Optional<User> user=userRepository.findByEmail(userDetails.getUsername());
        TeamDto teamDTO = teamService.findByName(teamname,user.get().getId());
        if(teamDTO==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no team by that name");
        }

        return ResponseEntity.ok(teamDTO);

    }

    @GetMapping("/teams")
    public ResponseEntity<?> findteamsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> user=userRepository.findByEmail(userDetails.getUsername());
        List<TeamDto> teams=teamService.findByUser(user.get());
        if(teams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("you do not have any teams");
        }
        return ResponseEntity.ok(teamService.findByUser(user.get()));
    }

    private TeamDto convertToDTO(Team team) {
        TeamDto teamDTO = new TeamDto();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setDateCreation(team.getDateCreation());
        teamDTO.setUsers(
                team.getUsers().stream().map(user -> {
                    UserResponse userDTO = new UserResponse();
                    userDTO.setId(user.getId());
                    userDTO.setName(user.getName());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setDateInscription(user.getDateInscription());
                    userDTO.setUserRole(user.getUserRole().toString());
                    return userDTO;
                }).collect(Collectors.toList())
        );
        return teamDTO;
    }

}