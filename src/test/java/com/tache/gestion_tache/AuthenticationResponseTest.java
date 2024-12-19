package com.tache.gestion_tache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tache.gestion_tache.dto.AuthenticationResponse;
import com.tache.gestion_tache.entities.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationResponseTest {

    @Test
    void testSerialization() throws Exception {
        // Prepare the test data
        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwt("some-jwt-token");
        response.setUserId(1L);
        response.setUserRole(UserRole.ADMIN);

        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Serialize the object to JSON
        String json = objectMapper.writeValueAsString(response);

        // Check if the expected values are present in the JSON string
        assertTrue(json.contains("some-jwt-token"));
        assertTrue(json.contains("1"));
        assertTrue(json.contains("ADMIN"));
    }

    @Test
    void testDeserialization() throws Exception {
        // Prepare the JSON string
        String json = "{" +
                "\"jwt\":\"some-jwt-token\"," +
                "\"userId\":1," +
                "\"userRole\":\"ADMIN\"" +
                "}";

        // Create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserialize the JSON string into AuthenticationResponse
        AuthenticationResponse response = objectMapper.readValue(json, AuthenticationResponse.class);

        // Assert the deserialized object contains the expected values
        assertEquals("some-jwt-token", response.getJwt());
        assertEquals(1L, response.getUserId());
        assertEquals(UserRole.ADMIN, response.getUserRole());
    }
}
