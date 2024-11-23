package com.tache.gestion_tache.dto;

import com.tache.gestion_tache.entities.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private String jwt;

    private long userId;

    private UserRole userRole;

}