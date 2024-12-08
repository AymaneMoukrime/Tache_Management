package com.tache.gestion_tache.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Email
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
