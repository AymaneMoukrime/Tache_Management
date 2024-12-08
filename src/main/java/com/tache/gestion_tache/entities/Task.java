package com.tache.gestion_tache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.DATE)
    private Date dateCreation=new Date();

    @Temporal(TemporalType.DATE)
    private Date dateDeadline;

    private Long priorite;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = true)
    private String couleur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

   @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


    public void assignUser(User user) {
        this.user = user;
        if (!user.getTasks().contains(this)) {
            user.getTasks().add(this);
        }
    }
    
}
