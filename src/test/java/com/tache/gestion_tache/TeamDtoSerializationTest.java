package com.tache.gestion_tache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TeamDtoSerializationTest {

    @Test
    void testSerialization() throws Exception {
        // Create a TeamDto object
        UserResponse user1 = new UserResponse(1L, "Alice", "alice@example.com", new Date(), "NORMAL");
        UserResponse user2 = new UserResponse(2L, "Bob", "bob@example.com", new Date(), "ADMIN");

        TeamDto teamDto = new TeamDto(1L, "Development Team", new Date(), Arrays.asList(user1, user2));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(teamDto);

        // Debugging: Print the serialized JSON string to inspect
        System.out.println("Serialized JSON: " + json);

        // Check if the expected values are present in the JSON
        assertTrue(json.contains("Development Team"), "Expected 'Development Team' to be in the JSON string");
        assertTrue(json.contains("Alice"), "Expected 'Alice' to be in the JSON string");
        assertTrue(json.contains("alice@example.com"), "Expected 'alice@example.com' to be in the JSON string");
        assertTrue(json.contains("Bob"), "Expected 'Bob' to be in the JSON string");
        assertTrue(json.contains("bob@example.com"), "Expected 'bob@example.com' to be in the JSON string");
    }

    @Test
    void testDeserialization() throws Exception {
        // Manually create a JSON string to match your expected format
        String json = "{\"id\":1,\"name\":\"Development Team\",\"dateCreation\":\"2024-12-18T00:00:00.000+00:00\",\"users\":[{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\",\"dateInscription\":\"2024-12-18T00:00:00.000+00:00\",\"userRole\":\"NORMAL\"},{\"id\":2,\"name\":\"Bob\",\"email\":\"bob@example.com\",\"dateInscription\":\"2024-12-18T00:00:00.000+00:00\",\"userRole\":\"ADMIN\"}]}";

        ObjectMapper objectMapper = new ObjectMapper();
        TeamDto teamDto = objectMapper.readValue(json, TeamDto.class);

        // Assert the deserialized values
        assertEquals(1L, teamDto.getId());
        assertEquals("Development Team", teamDto.getName());
        assertNotNull(teamDto.getDateCreation());  // The date should not be null
        assertEquals(2, teamDto.getUsers().size()); // There should be two users
        assertEquals("Alice", teamDto.getUsers().get(0).getName());
        assertEquals("Bob", teamDto.getUsers().get(1).getName());
    }
}
