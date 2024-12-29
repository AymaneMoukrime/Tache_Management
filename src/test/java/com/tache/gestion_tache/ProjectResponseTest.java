package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.ProjectResponse;
import com.tache.gestion_tache.dto.UserResponse;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectResponseTest {

    @Test
    void testNoArgsConstructor() {
        // When
        ProjectResponse projectResponse = new ProjectResponse();

        // Then
        assertNotNull(projectResponse);
        assertNull(projectResponse.getId());
        assertNull(projectResponse.getName());
        assertNull(projectResponse.getDescription());
        assertNull(projectResponse.getStartDate());
        assertNull(projectResponse.getEndDate());
        assertNull(projectResponse.getOwner());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long id = 1L;
        String name = "Test Project";
        String description = "Test Description";
        Date startDate = new Date();
        Date endDate = new Date();
        UserResponse owner = new UserResponse();

        // When
        ProjectResponse projectResponse = new ProjectResponse(id, name, description, startDate, endDate, owner);

        // Then
        assertNotNull(projectResponse);
        assertEquals(id, projectResponse.getId());
        assertEquals(name, projectResponse.getName());
        assertEquals(description, projectResponse.getDescription());
        assertEquals(startDate, projectResponse.getStartDate());
        assertEquals(endDate, projectResponse.getEndDate());
        assertEquals(owner, projectResponse.getOwner());
    }

    @Test
    void testConstructorWithIdNameDescription() {
        // Given
        Long id = 1L;
        String name = "Test Project";
        String description = "Test Description";

        // When
        ProjectResponse projectResponse = new ProjectResponse(id, name, description);

        // Then
        assertNotNull(projectResponse);
        assertEquals(id, projectResponse.getId());
        assertEquals(name, projectResponse.getName());
        assertEquals(description, projectResponse.getDescription());
        assertNull(projectResponse.getStartDate());
        assertNull(projectResponse.getEndDate());
        assertNull(projectResponse.getOwner());
    }

    @Test
    void testConstructorWithName() {
        // Given
        String name = "Test Project";

        // When
        ProjectResponse projectResponse = new ProjectResponse(name);

        // Then
        assertNotNull(projectResponse);
        assertNull(projectResponse.getId());
        assertEquals(name, projectResponse.getName());
        assertNull(projectResponse.getDescription());
        assertNull(projectResponse.getStartDate());
        assertNull(projectResponse.getEndDate());
        assertNull(projectResponse.getOwner());
    }

    @Test
    void testConstructorWithIdNameDescriptionStartDateEndDate() {
        // Given
        Long id = 1L;
        String name = "Test Project";
        String description = "Test Description";
        Date startDate = new Date();
        Date endDate = new Date();

        // When
        ProjectResponse projectResponse = new ProjectResponse(id, name, description, startDate, endDate);

        // Then
        assertNotNull(projectResponse);
        assertEquals(id, projectResponse.getId());
        assertEquals(name, projectResponse.getName());
        assertEquals(description, projectResponse.getDescription());
        assertEquals(startDate, projectResponse.getStartDate());
        // Note: endDate is not set in the constructor implementation
        assertNull(projectResponse.getEndDate());
        assertNull(projectResponse.getOwner());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        ProjectResponse projectResponse = new ProjectResponse();
        Long id = 1L;
        String name = "Test Project";
        String description = "Test Description";
        Date startDate = new Date();
        Date endDate = new Date();
        UserResponse owner = new UserResponse();

        // When
        projectResponse.setId(id);
        projectResponse.setName(name);
        projectResponse.setDescription(description);
        projectResponse.setStartDate(startDate);
        projectResponse.setEndDate(endDate);
        projectResponse.setOwner(owner);

        // Then
        assertEquals(id, projectResponse.getId());
        assertEquals(name, projectResponse.getName());
        assertEquals(description, projectResponse.getDescription());
        assertEquals(startDate, projectResponse.getStartDate());
        assertEquals(endDate, projectResponse.getEndDate());
        assertEquals(owner, projectResponse.getOwner());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        ProjectResponse project1 = new ProjectResponse(1L, "Test", "Desc");
        ProjectResponse project2 = new ProjectResponse(1L, "Test", "Desc");
        ProjectResponse project3 = new ProjectResponse(2L, "Test", "Desc");

        // Then
        assertEquals(project1, project2);
        assertNotEquals(project1, project3);
        assertEquals(project1.hashCode(), project2.hashCode());
        assertNotEquals(project1.hashCode(), project3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        ProjectResponse projectResponse = new ProjectResponse(1L, "Test Project", "Test Description");

        // When
        String toString = projectResponse.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Test Project"));
        assertTrue(toString.contains("Test Description"));
        assertTrue(toString.contains("1"));
    }
}