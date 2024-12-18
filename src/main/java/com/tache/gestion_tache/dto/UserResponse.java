package com.tache.gestion_tache.dto;

import com.tache.gestion_tache.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Date dateInscription;
    private String userRole;

    public UserResponse(String johnDoe, String mail) {
        this.name = johnDoe;
        this.email = mail;
    }
}
