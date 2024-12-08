package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public List<Team> findAll() {
        return teamRepository.findAll();
    }
@Override
    public TeamDto findByName(String name, Long userId) {
        return teamRepository.findByNameAndUserId(name, userId)
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


    
    private TeamDto mapToTeamDto(Team team) {
        List<UserResponse> userDTOs = team.getUsers().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getDateCreation(),
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
