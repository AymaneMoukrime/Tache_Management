package com.tache.gestion_tache.repositories;

import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    //only for admin
    Optional<User> findByUserRole(UserRole userRole);

    @Query("SELECT u.email FROM User u")
    List<String> getEmails();


}
