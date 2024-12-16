package com.tache.gestion_tache.config;


import com.tache.gestion_tache.entities.UserRole;
import com.tache.gestion_tache.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final JwtAuthentificationFilter jwtAuthentificationFilter;

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**").
                        permitAll().requestMatchers("/api/admin/**").hasAnyAuthority (UserRole.ADMIN.toString())
                        .requestMatchers("/api/normal/**","/api/task/**","/api/projet/**","/api/team/**","/api/user/**").hasAnyAuthority (UserRole.NORMAL.toString())
                        .anyRequest().authenticated()).sessionManagement(manager -> manager.sessionCreationPolicy (STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthentificationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
