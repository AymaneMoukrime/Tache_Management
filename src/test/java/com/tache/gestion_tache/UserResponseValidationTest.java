package com.tache.gestion_tache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tache.gestion_tache.dto.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseSerializationTest {

    @Test
    void testSerialization() throws Exception {
        UserResponse userResponse = new UserResponse(1L, "John Doe", "john.doe@example.com", new Date(), "ADMIN");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userResponse);

        // Debugging: Print the JSON string to inspect
        System.out.println("Serialized JSON: " + json);

        // Make sure the expected values are present
        assertTrue(json.contains("John Doe"), "Expected 'John Doe' to be in the JSON string");
        assertTrue(json.contains("john.doe@example.com"), "Expected 'john.doe@example.com' to be in the JSON string");
        assertTrue(json.contains("ADMIN"), "Expected 'ADMIN' to be in the JSON string");
    }

    @Test
    void testDeserialization() throws Exception {
        // Manually create the JSON string to match your expected format
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"dateInscription\":\"2024-12-18T00:00:00.000+00:00\",\"userRole\":\"ADMIN\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        UserResponse userResponse = objectMapper.readValue(json, UserResponse.class);

        // Assert the deserialized values
        assertEquals(1L, userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john.doe@example.com", userResponse.getEmail());
        assertEquals("ADMIN", userResponse.getUserRole());
    }
}

