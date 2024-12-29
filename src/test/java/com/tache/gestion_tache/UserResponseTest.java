package com.tache.gestion_tache;
import com.tache.gestion_tache.dto.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class UserResponseTest {

    @Test
    void testAllArgsConstructor() {
        Date dateInscription = new Date();
        UserResponse userResponse = new UserResponse(1L, "John Doe", "john@example.com", dateInscription, "ADMIN");

        assertEquals(1L, userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john@example.com", userResponse.getEmail());
        assertEquals(dateInscription, userResponse.getDateInscription());
        assertEquals("ADMIN", userResponse.getUserRole());
    }

    @Test
    void testNoArgsConstructor() {
        UserResponse userResponse = new UserResponse();

        assertNull(userResponse.getId());
        assertNull(userResponse.getName());
        assertNull(userResponse.getEmail());
        assertNull(userResponse.getDateInscription());
        assertNull(userResponse.getUserRole());
    }

    @Test
    void testConstructorWithNameAndEmail() {
        UserResponse userResponse = new UserResponse("John Doe", "john@example.com");

        assertNull(userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john@example.com", userResponse.getEmail());
        assertNull(userResponse.getDateInscription());
        assertNull(userResponse.getUserRole());
    }

    @Test
    void testSettersAndGetters() {
        Date dateInscription = new Date();
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("John Doe");
        userResponse.setEmail("john@example.com");
        userResponse.setDateInscription(dateInscription);
        userResponse.setUserRole("ADMIN");

        assertEquals(1L, userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john@example.com", userResponse.getEmail());
        assertEquals(dateInscription, userResponse.getDateInscription());
        assertEquals("ADMIN", userResponse.getUserRole());
    }
}
