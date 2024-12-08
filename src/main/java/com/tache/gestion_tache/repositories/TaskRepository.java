package com.tache.gestion_tache.repositories;

import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository  extends JpaRepository<Task, Long> {
    void deleteById(Long id);
    List<Task> findByProject(Project project);
}
