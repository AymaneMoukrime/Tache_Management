package com.tache.gestion_tache;

import com.tache.gestion_tache.dto.TeamDto;
import com.tache.gestion_tache.dto.UserResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

public class TeamDtoTest {

    @Test
    void testAllArgsConstructor() {
        Date dateCreation = new Date();
        List<UserResponse> users = Arrays.asList(new UserResponse(1L, "John Doe", "john@example.com", new Date(), "NORMAL"));
        TeamDto teamDto = new TeamDto(1L, "Team A", dateCreation, 1L, users);

        assertEquals(1L, teamDto.getId());
        assertEquals("Team A", teamDto.getName());
        assertEquals(dateCreation, teamDto.getDateCreation());
        assertEquals(1L, teamDto.getProjectid());
        assertEquals(users, teamDto.getUsers());
    }

    @Test
    void testNoArgsConstructor() {
        TeamDto teamDto = new TeamDto();

        assertNull(teamDto.getId());
        assertNull(teamDto.getName());
        assertNull(teamDto.getDateCreation());
        assertNull(teamDto.getProjectid());
        assertNull(teamDto.getUsers());
    }

    @Test
    void testConstructorWithIdNameDateCreation() {
        Date dateCreation = new Date();
        TeamDto teamDto = new TeamDto(1L, "Team A", dateCreation);

        assertEquals(1L, teamDto.getId());
        assertEquals("Team A", teamDto.getName());
        assertEquals(dateCreation, teamDto.getDateCreation());
        assertNull(teamDto.getProjectid());
        assertNull(teamDto.getUsers());
    }

    @Test
    void testConstructorWithIdNameDateCreationProjectid() {
        Date dateCreation = new Date();
        TeamDto teamDto = new TeamDto(1L, "Team A", dateCreation, 1L);

        assertEquals(1L, teamDto.getId());
        assertEquals("Team A", teamDto.getName());
        assertEquals(dateCreation, teamDto.getDateCreation());
        assertEquals(1L, teamDto.getProjectid());
        assertNull(teamDto.getUsers());
    }

    @Test
    void testConstructorWithIdName() {
        TeamDto teamDto = new TeamDto(1L, "Team A");

        assertEquals(1L, teamDto.getId());
        assertEquals("Team A", teamDto.getName());
        assertNull(teamDto.getDateCreation());
        assertNull(teamDto.getProjectid());
        assertNull(teamDto.getUsers());
    }
}
