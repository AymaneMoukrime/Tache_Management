package com.tache.gestion_tache.repositories;

import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByIdAndProjectId(Long id, Long projectId);
    List<Team> findByUsers(User users);
    @Query("SELECT t FROM Team t JOIN t.users u WHERE t.name = :name AND u.id = :userId AND t.project.id = :projectId")
    Optional<Team> findByNameAndUserIdAndProjectId(@Param("name") String name,
                                               @Param("userId") Long userId,
                                               @Param("projectId") Long projectId);
    @Query("SELECT t FROM Team t WHERE t.name = :name AND t.project.id = :projectId")
    Optional<Team> findByNameAndProjectId(@Param("name") String name, @Param("projectId") Long projectId);
}
