package com.tache.gestion_tache.services;

import com.tache.gestion_tache.entities.Project;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProjectService {
    Project addproject(UserDetails userDetails, Project project);
}
