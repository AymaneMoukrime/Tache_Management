package com.tache.gestion_tache.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;

    private String email;

    private String password;
}
