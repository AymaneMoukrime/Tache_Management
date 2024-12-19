package com.tache.gestion_tache;

import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.Team;
import com.tache.gestion_tache.entities.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void testDefaultValues() {
        Project project = new Project();

        assertNull(project.getId());
        assertNull(project.getName());
        assertNull(project.getDescription());
        assertNull(project.getStartDate());
        assertNull(project.getEndDate());
        assertNotNull(project.getTasks());
        assertTrue(project.getTasks().isEmpty());
        assertNotNull(project.getTeams());
        assertTrue(project.getTeams().isEmpty());
        assertNotNull(project.getUsers());
        assertTrue(project.getUsers().isEmpty());
        assertNull(project.getOwner());
    }

    @Test
    void testSetAndGetId() {
        Project project = new Project();
        project.setId(1L);

        assertEquals(1L, project.getId());
    }

    @Test
    void testSetAndGetName() {
        Project project = new Project();
        project.setName("New Project");

        assertEquals("New Project", project.getName());
    }

    @Test
    void testSetAndGetDescription() {
        Project project = new Project();
        project.setDescription("This is a project description.");

        assertEquals("This is a project description.", project.getDescription());
    }

    @Test
    void testSetAndGetStartDate() {
        Project project = new Project();
        Date startDate = new Date(1672531200000L); // Example date: 2023-01-01

        project.setStartDate(startDate);

        assertEquals(startDate, project.getStartDate());
    }

    @Test
    void testSetAndGetEndDate() {
        Project project = new Project();
        Date endDate = new Date(1675119600000L); // Example date: 2023-02-01

        project.setEndDate(endDate);

        assertEquals(endDate, project.getEndDate());
    }

    @Test
    void testSetAndGetOwner() {
        Project project = new Project();
        User owner = new User();
        owner.setId(1L);

        project.setOwner(owner);

        assertEquals(owner, project.getOwner());
    }

    @Test
    void testAddAndRemoveTask() {
        Project project = new Project();
        Task task = new Task();
        task.setId(1L);

        project.getTasks().add(task);
        assertTrue(project.getTasks().contains(task));

        project.getTasks().remove(task);
        assertFalse(project.getTasks().contains(task));
    }

    @Test
    void testAddAndRemoveTeam() {
        Project project = new Project();
        Team team = new Team();
        team.setId(1L);

        project.getTeams().add(team);
        assertTrue(project.getTeams().contains(team));

        project.getTeams().remove(team);
        assertFalse(project.getTeams().contains(team));
    }

    @Test
    void testAddAndRemoveUser() {
        Project project = new Project();
        User user = new User();
        user.setId(1L);

        project.getUsers().add(user);
        assertTrue(project.getUsers().contains(user));

        project.getUsers().remove(user);
        assertFalse(project.getUsers().contains(user));
    }

    @Test
    void testEqualsAndHashCode() {
        Project project1 = new Project();
        project1.setId(1L);

        Project project2 = new Project();
        project2.setId(1L);

        assertEquals(project1, project2);
        assertEquals(project1.hashCode(), project2.hashCode());

        project2.setId(2L);

        assertNotEquals(project1, project2);
        assertNotEquals(project1.hashCode(), project2.hashCode());
    }

    @Test
    void testToString() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Sample Project");

        String projectString = project.toString();

        assertTrue(projectString.contains("id=1"));
        assertTrue(projectString.contains("name=Sample Project"));
    }
}
