package com.tache.gestion_tache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
    private Long priorite=1L;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(nullable = true)
    private String couleur;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

   @ManyToOne
    @JoinColumn(name = "project_id")
   @JsonIgnore
    private Project project;


    public void assignUser(User user) {
        this.user = user;
        if (!user.getTasks().contains(this)) {
            user.getTasks().add(this);
        }
    }
    
}
