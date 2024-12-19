package com.tache.gestion_tache;

import com.tache.gestion_tache.entities.Project;
import com.tache.gestion_tache.entities.Task;
import com.tache.gestion_tache.entities.TaskStatus;
import com.tache.gestion_tache.entities.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testDefaultValues() {
        Task task = new Task();

        assertNotNull(task.getDateCreation());
        assertEquals(1L, task.getPriorite());
        assertNull(task.getTitre());
        assertNull(task.getDescription());
        assertNull(task.getDateDeadline());
        assertNull(task.getStatus());
        assertNull(task.getCouleur());
        assertNull(task.getOwner());
        assertNull(task.getUser());
        assertNull(task.getProject());
    }

    @Test
    void testSetAndGetId() {
        Task task = new Task();
        task.setId(1L);

        assertEquals(1L, task.getId());
    }

    @Test
    void testSetAndGetTitre() {
        Task task = new Task();
        task.setTitre("Task 1");

        assertEquals("Task 1", task.getTitre());
    }

    @Test
    void testSetAndGetDescription() {
        Task task = new Task();
        task.setDescription("This is a sample task description.");

        assertEquals("This is a sample task description.", task.getDescription());
    }

    @Test
    void testSetAndGetDateCreation() {
        Task task = new Task();
        Date customDate = new Date(1672531200000L); // Example date: 2023-01-01

        task.setDateCreation(customDate);

        assertEquals(customDate, task.getDateCreation());
    }

    @Test
    void testSetAndGetDateDeadline() {
        Task task = new Task();
        Date deadline = new Date(1675119600000L); // Example deadline: 2023-02-01

        task.setDateDeadline(deadline);

        assertEquals(deadline, task.getDateDeadline());
    }

    @Test
    void testSetAndGetPriorite() {
        Task task = new Task();
        task.setPriorite(5L);

        assertEquals(5L, task.getPriorite());
    }

    @Test
    void testSetAndGetStatus() {
        Task task = new Task();
        TaskStatus status = TaskStatus.IN_PROGRESS;

        task.setStatus(status);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void testSetAndGetCouleur() {
        Task task = new Task();
        task.setCouleur("#FF5733");

        assertEquals("#FF5733", task.getCouleur());
    }

    @Test
    void testSetAndGetOwner() {
        Task task = new Task();
        User owner = new User();
        owner.setId(1L);

        task.setOwner(owner);

        assertEquals(owner, task.getOwner());
    }

    @Test
    void testSetAndGetUser() {
        Task task = new Task();
        User user = new User();
        user.setId(2L);

        task.setUser(user);

        assertEquals(user, task.getUser());
    }

    @Test
    void testAssignUser() {
        Task task = new Task();
        User user = new User();

        task.assignUser(user);

        assertEquals(user, task.getUser());
        assertTrue(user.getTasks().contains(task));
    }

    @Test
    void testSetAndGetProject() {
        Task task = new Task();
        Project project = new Project();
        project.setId(1L);

        task.setProject(project);

        assertEquals(project, task.getProject());
    }

    @Test
    void testEqualsAndHashCode() {
        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(1L);

        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());

        task2.setId(2L);

        assertNotEquals(task1, task2);
        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testToString() {
        Task task = new Task();
        task.setId(1L);
        task.setTitre("Sample Task");

        String taskString = task.toString();

        assertTrue(taskString.contains("id=1"));
        assertTrue(taskString.contains("titre=Sample Task"));
    }
}
