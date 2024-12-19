package com.tache.gestion_tache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tache.gestion_tache.dto.TaskDto;
import com.tache.gestion_tache.entities.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskDtoSerializationTest {

    @Test
    void testSerialization() throws Exception {
        // Create a TaskDto object
        TaskDto taskDto = new TaskDto(
                1L,
                "Task Title",
                "Task Description",
                new Date(),
                new Date(),
                1L,
                TaskStatus.IN_PROGRESS, // Example status
                "red",
                "owner@example.com",
                "user@example.com"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(taskDto);

        // Debugging: Print the serialized JSON string to inspect
        System.out.println("Serialized JSON: " + json);

        // Check if the expected values are present in the JSON
        assertTrue(json.contains("Task Title"), "Expected 'Task Title' to be in the JSON string");
        assertTrue(json.contains("Task Description"), "Expected 'Task Description' to be in the JSON string");
        assertTrue(json.contains("owner@example.com"), "Expected 'owner@example.com' to be in the JSON string");
        assertTrue(json.contains("user@example.com"), "Expected 'user@example.com' to be in the JSON string");
        assertTrue(json.contains("IN_PROGRESS"), "Expected 'IN_PROGRESS' status to be in the JSON string");
    }

}
