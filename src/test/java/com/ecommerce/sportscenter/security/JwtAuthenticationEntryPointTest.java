package com.ecommerce.sportscenter.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    @DisplayName("Should set unauthorized status and write error message")
    void commence_ShouldSetUnauthorizedStatus() throws Exception {
        // Arrange
        when(authException.getMessage()).thenReturn("Invalid credentials");
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).getWriter();
        printWriter.flush();
        assertThat(stringWriter.toString()).contains("Access Denied");
        assertThat(stringWriter.toString()).contains("Invalid credentials");
    }

    @Test
    @DisplayName("Should handle null auth exception message")
    void commence_WithNullMessage_ShouldHandleGracefully() throws Exception {
        // Arrange
        when(authException.getMessage()).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).getWriter();
        printWriter.flush();
        assertThat(stringWriter.toString()).contains("Access Denied");
    }

    @Test
    @DisplayName("Should handle different error messages")
    void commence_WithDifferentMessages_ShouldWriteCorrectly() throws Exception {
        // Arrange
        when(authException.getMessage()).thenReturn("Token expired");
        when(response.getWriter()).thenReturn(printWriter);

        // Act
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        printWriter.flush();
        assertThat(stringWriter.toString()).contains("Token expired");
    }
}
