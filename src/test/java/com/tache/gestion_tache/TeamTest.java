package com.tache.gestion_tache;

import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    @Test
    void testDefaultValues() {
        Team team = new Team();

        assertNotNull(team.getDateCreation());
        assertNotNull(team.getUsers());
        assertTrue(team.getUsers().isEmpty());
        assertNull(team.getProject());
    }

    @Test
    void testSetAndGetId() {
        Team team = new Team();
        team.setId(1L);

        assertEquals(1L, team.getId());
    }

    @Test
    void testSetAndGetName() {
        Team team = new Team();
        team.setName("Development Team");

        assertEquals("Development Team", team.getName());
    }

    @Test
    void testSetAndGetDateCreation() {
        Team team = new Team();
        Date customDate = new Date(1672531200000L); // Example date: 2023-01-01

        team.setDateCreation(customDate);

        assertEquals(customDate, team.getDateCreation());
    }

    @Test
    void testAddAndRemoveUsers() {
        Team team = new Team();
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        // Adding users
        team.getUsers().add(user1);
        team.getUsers().add(user2);

        assertEquals(2, team.getUsers().size());
        assertTrue(team.getUsers().contains(user1));
        assertTrue(team.getUsers().contains(user2));

        // Removing a user
        team.getUsers().remove(user1);

        assertEquals(1, team.getUsers().size());
        assertFalse(team.getUsers().contains(user1));
        assertTrue(team.getUsers().contains(user2));
    }

    @Test
    void testSetAndGetProject() {
        Team team = new Team();
        Project project = new Project();
        project.setId(1L);
        project.setName("Project Alpha");

        team.setProject(project);

        assertEquals(project, team.getProject());
        assertEquals("Project Alpha", team.getProject().getName());
    }

    @Test
    void testEqualsAndHashCode() {
        Team team1 = new Team();
        team1.setId(1L);

        Team team2 = new Team();
        team2.setId(1L);

        assertEquals(team1, team2);
        assertEquals(team1.hashCode(), team2.hashCode());

        team2.setId(2L);

        assertNotEquals(team1, team2);
        assertNotEquals(team1.hashCode(), team2.hashCode());
    }

    @Test
    void testToString() {
        Team team = new Team();
        team.setId(1L);
        team.setName("QA Team");

        String teamString = team.toString();

        assertTrue(teamString.contains("id=1"));
        assertTrue(teamString.contains("name=QA Team"));
    }
}
