package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public List<Team> findAll() {
        return teamRepository.findAll();
    }
@Override
    public TeamDto findByName(String name, Long userId,Long projectid) {
        return teamRepository.findByNameAndUserIdAndProjectId(name, userId,projectid)
                .map(this::mapToTeamDto)
                .orElse(null); // or throw an exception like new NotFoundException("Team not found")
    }

    @Override
    public TeamDto findByid(Long teamid) throws RuntimeException {
        return teamRepository.findById(teamid)
                .map(this::mapToTeamDto)
                .orElse(null); // or throw an exception like new NotFoundException("Team not found")
    }
@Override
    public List<TeamDto> findByUser(User user) {
        List<Team> teams = teamRepository.findByUsers(user);
        return teams.stream()
                .map(this::mapToTeamDto)
                .collect(Collectors.toList());
    }

@Override
    public List<TeamDto> findAlladmin() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
@Override
    public TeamDto findById(Long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);
        return team.map(this::convertToDTO).orElse(null); // Return null if team not found
    }
    // Convert Team to TeamDto
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


    
    private TeamDto mapToTeamDto(Team team) {
        List<UserResponse> userDTOs = team.getUsers().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getDateCreation(),
                team.getProject().getId(),
                userDTOs
        );
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDateInscription(),
                user.getUserRole().toString()
        );
    }
}
