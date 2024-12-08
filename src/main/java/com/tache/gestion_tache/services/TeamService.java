package com.tache.gestion_tache.services;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.entities.User;

import java.util.List;

public interface TeamService {

    TeamDto findByName(String name, Long userId);
    List<TeamDto> findByUser(User user);

}