package com.tache.gestion_tache;

import com.tache.gestion_tache.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String TEST_USERNAME = "testUser";
    private final Long TEST_USER_ID = 1L;
    private final String TEST_ROLE = "ROLE_USER";
    private final long ONE_HOUR = 3600000L;

    @BeforeEach
    void setUp() {
        // Set expiration time using reflection since @Value won't work in unit tests
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 3600000L); // 1 hour
    }

    private UserDetails createMockUserDetails() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(TEST_USERNAME);
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority(TEST_ROLE)
        );
        when(userDetails.getAuthorities()).thenReturn((List) authorities);
        return userDetails;
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        UserDetails userDetails = createMockUserDetails();

        // When
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertEquals(TEST_USERNAME, jwtUtil.extractUserName(token));
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        // Given
        UserDetails userDetails = createMockUserDetails();
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        // When
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }


    @Test
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        // Given
        UserDetails userDetails = createMockUserDetails();
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        UserDetails differentUser = mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("differentUser");

        // When
        boolean isValid = jwtUtil.isTokenValid(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractAllClaims_ShouldExtractCorrectClaims() {
        // Given
        UserDetails userDetails = createMockUserDetails();
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        // When
        Claims claims = jwtUtil.extractAllClaims(token);

        // Then
        assertEquals(TEST_USERNAME, claims.getSubject());
        assertNotNull(claims.get("roles"));
        assertEquals(TEST_USER_ID, ((Number) claims.get("id")).longValue());
    }

    @Test
    void extractAllClaims_ShouldThrowException_ForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.extractAllClaims(invalidToken);
        });
    }

    @Test
    void extractExpiration_ShouldReturnCorrectDate() {
        // Given
        UserDetails userDetails = createMockUserDetails();
        String token = jwtUtil.generateToken(userDetails, TEST_USER_ID);

        // When
        Date expirationDate = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}