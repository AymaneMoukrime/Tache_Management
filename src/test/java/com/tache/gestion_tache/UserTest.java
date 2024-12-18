package com.tache.gestion_tache;

import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import com.tache.gestion_tache.entities.UserRole;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testAddTask() {
        User user = new User();
        Task task = new Task();

        user.addTask(task);

        assertEquals(1, user.getTasks().size());
        assertEquals(task, user.getTasks().get(0));
        assertEquals(user, task.getUser());
    }

    @Test
    void testRemoveTask() {
        User user = new User();
        Task task = new Task();

        user.addTask(task);
        user.removeTask(task);

        assertTrue(user.getTasks().isEmpty());
        assertNull(task.getUser());
    }

    @Test
    void testAddTeam() {
        User user = new User();
        Team team = new Team();
        team.setUsers(new ArrayList<>());

        user.addTeam(team);

        assertTrue(user.getTeams().contains(team));
        assertTrue(team.getUsers().contains(user));
    }

    @Test
    void testRemoveTeam() {
        User user = new User();
        Team team = new Team();
        team.setUsers(new ArrayList<>());

        user.addTeam(team);
        user.removeTeam(team);

        assertFalse(user.getTeams().contains(team));
        assertFalse(team.getUsers().contains(user));
    }

    @Test
    void testGetAuthorities() {
        User user = new User();
        user.setUserRole(UserRole.ADMIN);

        assertEquals(1, user.getAuthorities().size());
        assertEquals("ROLE_ADMIN", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void testGetUsername() {
        User user = new User();
        user.setEmail("test@example.com");

        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        User user = new User();
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        User user = new User();
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        User user = new User();
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        User user = new User();
        assertTrue(user.isEnabled());
    }

    @Test
    void testDefaultValues() {
        User user = new User();
        assertNotNull(user.getDateInscription());
        assertTrue(user.getTasks().isEmpty());
        assertTrue(user.getTeams().isEmpty());
        assertTrue(user.getProjects().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        user2.setId(2L);
        assertNotEquals(user1, user2);
    }

    @Test
    void testPasswordResetFields() {
        User user = new User();
        Date expiryDate = new Date();
        String resetCode = "ABC123";

        user.setPasswordResetCode(resetCode);
        user.setPasswordResetCodeExpiry(expiryDate);

        assertEquals(resetCode, user.getPasswordResetCode());
        assertEquals(expiryDate, user.getPasswordResetCodeExpiry());
    }
}
