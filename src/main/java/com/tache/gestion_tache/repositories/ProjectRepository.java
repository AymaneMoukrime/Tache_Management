package com.tache.gestion_tache.repositories;

import com.tache.gestion_tache.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN p.users u " +
            "LEFT JOIN p.teams t " +
            "LEFT JOIN t.users tu " + // Joining the users of the team
            "WHERE p.owner.id = :userId OR u.id = :userId OR tu.id = :userId")
    List<Project> findAllByUserAccess(@Param("userId") Long userId);

    @Query("SELECT u.email FROM Project p JOIN p.users u WHERE p.id = :projectId")
    List<String> findAllUsersMailByProjectId(@Param("projectId") Long projectId);
}
