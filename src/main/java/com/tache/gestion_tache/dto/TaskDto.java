package com.tache.gestion_tache.dto;

import com.tache.gestion_tache.entities.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TaskDto {
        private Long id;
        private String title;
        private String description;
        private Date  dateCreation;
        private Date  dateDeadline;
        private Long priorite;
        private TaskStatus status;
        private String couleur;
        private String taskOwnerEmail;
        private String taskUserEmail;

}
