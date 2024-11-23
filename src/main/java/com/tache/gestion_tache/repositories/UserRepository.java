package com.tache.gestion_tache.repositories;

import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserRole(UserRole userRole);

}
