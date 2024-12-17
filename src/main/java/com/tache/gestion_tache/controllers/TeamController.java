package com.tache.gestion_tache.controllers;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.ProjectRepository;
import com.tache.gestion_tache.repositories.TeamRepository;
import com.tache.gestion_tache.repositories.UserRepository;
import com.tache.gestion_tache.services.TeamService;
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
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamService teamService;


    @PostMapping("/create_team/{projectid}")
    public ResponseEntity<?> createTeam(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long projectid, @RequestBody Team team) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User doesn't exist"));

        Project project = projectRepository.findById(projectid)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwner().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of this project");
        }

        // Check if a team with the same name already exists in the project
        Optional<Team> existingTeam = teamRepository.findByNameAndProjectId(team.getName(), projectid);
        if (existingTeam.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A team with this name already exists in the project");
        }

        // Set the creation date and associate the team with the project
        team.setDateCreation(new Date());
        team.getUsers().add(user);
        team.setProject(project);

        // Save the new team
        Team savedTeam = teamRepository.save(team);

        // Convert to DTO and return the response
        TeamDto teamDTO = convertToDTO(savedTeam);
        return ResponseEntity.ok(teamDTO);
    }


    @GetMapping("/teamname/{projectid}/{teamname}")
    public ResponseEntity<?> findteambyname(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid,@PathVariable String teamname) {
        User user=userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));
        TeamDto teamDTO = teamService.findByName(teamname,user.getId(),projectid);
        if(teamDTO==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no team by that name");
        }

        return ResponseEntity.ok(teamDTO);
    }
    @PostMapping("/asignUsertoTeam/{projectid}/{teamid}")
    public  ResponseEntity<?> assignUserToTeam(@AuthenticationPrincipal UserDetails userDetails,@PathVariable Long projectid,@PathVariable Long teamid,@RequestParam String Email) {
        Project project=projectRepository.findById(projectid).orElseThrow(() -> new RuntimeException("Project not found"));
        User user=userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        Team team=teamRepository.findById(teamid).orElseThrow(() -> new RuntimeException("Team not found"));
        if(!project.getOwner().equals(user)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have permission to assign this team");
        }
        User adduser=userRepository.findByEmail(Email).orElseThrow(() -> new RuntimeException("User not found"));
        if(team.getUsers().contains(adduser)){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already assigned to this team");
        }
        team.getUsers().add(adduser);
        teamRepository.save(team);
        TeamDto teamDTO = convertToDTO(team);

        return ResponseEntity.ok(teamDTO);
    }
    @PostMapping("/removeUserFromTeam/{projectid}/{teamid}")
    public ResponseEntity<?> removeUserFromTeam(@AuthenticationPrincipal UserDetails userDetails,
                                                @PathVariable Long projectid,
                                                @PathVariable Long teamid,
                                                @RequestParam String Email) {
        // Find the project by ID
        Project project = projectRepository.findById(projectid)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Find the currently authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the authenticated user is the owner of the project
        if (!project.getOwner().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have permission to remove a user from this team");
        }

        // Find the team by ID
        Team team = teamRepository.findById(teamid)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Find the user to be removed by email
        User removeUser = userRepository.findByEmail(Email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is part of the team
        if (!team.getUsers().contains(removeUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not assigned to this team");
        }

        // Remove the user from the team
        team.getUsers().remove(removeUser);
        teamRepository.save(team);

        // Convert the updated team to DTO and return it
        TeamDto teamDTO = convertToDTO(team);
        return ResponseEntity.ok(teamDTO);
    }
    @DeleteMapping("/delete_team/{projectid}/{teamid}")
    public ResponseEntity<?> deleteTeam(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable Long projectid,
                                        @PathVariable Long teamid) {
        // Fetch the logged-in user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Find the project by ID
        Project project = projectRepository.findById(projectid)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the logged-in user is the owner of the project
        if (!project.getOwner().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of this project");
        }

        // Find the team by ID and project ID
        Team team = teamRepository.findByIdAndProjectId(teamid, projectid)
                .orElseThrow(() -> new RuntimeException("Team not found in this project"));

        // Delete the team
        teamRepository.delete(team);

        // Return a successful response
        return ResponseEntity.ok("Team successfully deleted");
    }




    @GetMapping("/teams")
    public ResponseEntity<?> findteamsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user=userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found with email: " + userDetails.getUsername()));;
        List<TeamDto> teams=teamService.findByUser(user);
        if(teams.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("you do not have any teams");
        }
        return ResponseEntity.ok(teamService.findByUser(user));
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