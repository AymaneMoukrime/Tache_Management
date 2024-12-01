package com.tache.gestion_tache.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
        private Long id;
        private String name;
        private Date dateCreation;
        private List<UserResponse> users;

        public TeamDto(Long id, String name, Date dateCreation) {
                this.id = id;
                this.name = name;
                this.dateCreation = dateCreation;
        }
}
