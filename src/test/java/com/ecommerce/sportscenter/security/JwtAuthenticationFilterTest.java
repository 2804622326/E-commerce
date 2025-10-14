package com.ecommerce.sportscenter.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        testUserDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtHelper.getUserNameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
        when(jwtHelper.validateToken(token, testUserDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(testUserDetails);

        verify(jwtHelper).getUserNameFromToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtHelper).validateToken(token, testUserDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtHelper.getUserNameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
        when(jwtHelper.validateToken(token, testUserDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtHelper).getUserNameFromToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtHelper).validateToken(token, testUserDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtHelper, never()).getUserNameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNonBearerToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String authHeader = "Basic some.credentials";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtHelper, never()).getUserNameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "expired.jwt.token";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtHelper.getUserNameFromToken(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtHelper).getUserNameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "malformed.jwt.token";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtHelper.getUserNameFromToken(token)).thenThrow(new MalformedJwtException("Malformed token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtHelper).getUserNameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithIllegalArgumentException_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "illegal.jwt.token";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtHelper.getUserNameFromToken(token)).thenThrow(new IllegalArgumentException("Illegal argument"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtHelper).getUserNameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExistingAuthentication_ShouldNotUpdateAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        
        // Set existing authentication
        UserDetails existingUser = new User("existinguser", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        existingUser, null, existingUser.getAuthorities()
                )
        );

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtHelper.getUserNameFromToken(token)).thenReturn("testuser");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(existingUser);

        verify(jwtHelper).getUserNameFromToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtHelper, never()).validateToken(anyString(), any(UserDetails.class));
        verify(filterChain).doFilter(request, response);
    }
}
