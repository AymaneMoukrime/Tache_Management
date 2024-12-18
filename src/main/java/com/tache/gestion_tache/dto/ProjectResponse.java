package com.tache.gestion_tache.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tache.gestion_tache.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private UserResponse Owner;

    public ProjectResponse(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public ProjectResponse(String name) {
        this.name=name;
    }

    public ProjectResponse(Long id, String name, String description, Date startDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }
}
