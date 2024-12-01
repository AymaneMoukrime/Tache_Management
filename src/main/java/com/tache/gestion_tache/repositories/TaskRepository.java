package com.tache.gestion_tache.repositories;

import com.tache.gestion_tache.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository  extends JpaRepository<Task, Long> {
}
