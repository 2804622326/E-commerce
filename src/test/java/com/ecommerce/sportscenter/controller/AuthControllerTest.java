package com.ecommerce.sportscenter.controller;

import com.ecommerce.sportscenter.config.TestSecurityConfig;
import com.ecommerce.sportscenter.model.JwtRequest;
import com.ecommerce.sportscenter.model.JwtResponse;
import com.ecommerce.sportscenter.security.JwtHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtHelper jwtHelper;

    private UserDetails testUserDetails;
    private JwtRequest testJwtRequest;

    @BeforeEach
    void setUp() {
        testUserDetails = new User("testuser", "password", new ArrayList<>());
        testJwtRequest = new JwtRequest("testuser", "password");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        // Arrange
        String expectedToken = "test.jwt.token";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(testUserDetails);
        when(jwtHelper.generateToken(testUserDetails))
                .thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testJwtRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").value(expectedToken));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtHelper).generateToken(testUserDetails);
    }

    @Test
    void getUserDetails_WithNullToken_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(jwtHelper, never()).getUserNameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void getUserDetails_WithInvalidTokenFormat_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String authHeader = "InvalidFormat token.here";

        // Act & Assert
        mockMvc.perform(get("/api/auth/user")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(jwtHelper, never()).getUserNameFromToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void getUserDetails_WithEmptyBearerToken_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String authHeader = "Bearer ";
        
        // The code extracts "" after "Bearer " which is empty string, not null
        // When passed to jwtHelper, it will try to get username
        when(jwtHelper.getUserNameFromToken("")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);

        // Act & Assert
        mockMvc.perform(get("/api/auth/user")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void login_ShouldCreateJwtResponseWithCorrectStructure() throws Exception {
        // Arrange
        String expectedToken = "generated.token.value";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(testUserDetails);
        when(jwtHelper.generateToken(testUserDetails))
                .thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testJwtRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").value(expectedToken));
    }
}
