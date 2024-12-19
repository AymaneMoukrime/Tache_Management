package com.tache.gestion_tache;

import com.tache.gestion_tache.config.JwtAuthentificationFilter;
import com.tache.gestion_tache.services.UserService;
import com.tache.gestion_tache.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.Filter;
import java.lang.reflect.Method;
import java.io.IOException;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthentificationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthentificationFilter jwtAuthentificationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void invokeDoFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws Exception {
        Method method = JwtAuthentificationFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true); // Make the method accessible even though it's protected
        method.invoke(jwtAuthentificationFilter, request, response, filterChain);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException, Exception {
        // Given no Authorization header
        when(request.getHeader("Authorization")).thenReturn(null);

        // When filter is applied
        invokeDoFilterInternal(request, response, filterChain);

        // Then it should simply pass the request through
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidAuthorizationHeader() throws ServletException, IOException, Exception {
        // Given a malformed Authorization header
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        // When filter is applied
        invokeDoFilterInternal(request, response, filterChain);

        // Then it should simply pass the request through
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


}
