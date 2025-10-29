package com.ecommerce.sportscenter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtHelperTest {

    @InjectMocks
    private JwtHelper jwtHelper;

    private UserDetails userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username("testuser@test.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        validToken = jwtHelper.generateToken(userDetails);
    }

    @Test
    @DisplayName("Should generate token successfully")
    void generateToken_ShouldReturnValidToken() {
        // Act
        String token = jwtHelper.generateToken(userDetails);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should extract username from token")
    void getUserNameFromToken_ShouldReturnUsername() {
        // Act
        String username = jwtHelper.getUserNameFromToken(validToken);

        // Assert
        assertThat(username).isEqualTo("testuser@test.com");
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void getExpirationDateFromToken_ShouldReturnFutureDate() {
        // Act
        Date expirationDate = jwtHelper.getExpirationDateFromToken(validToken);

        // Assert
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(new Date());
    }

    @Test
    @DisplayName("Should validate token successfully")
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Act
        Boolean isValid = jwtHelper.validateToken(validToken, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token with wrong username")
    void validateToken_WithWrongUsername_ShouldReturnFalse() {
        // Arrange
        UserDetails differentUser = User.builder()
                .username("different@test.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // Act
        Boolean isValid = jwtHelper.validateToken(validToken, differentUser);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void generateToken_ForDifferentUsers_ShouldReturnDifferentTokens() {
        // Arrange
        UserDetails user1 = User.builder()
                .username("user1@test.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        UserDetails user2 = User.builder()
                .username("user2@test.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // Act
        String token1 = jwtHelper.generateToken(user1);
        String token2 = jwtHelper.generateToken(user2);

        // Assert
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should extract correct username from generated token")
    void generateAndExtractUsername_ShouldMatch() {
        // Arrange
        UserDetails customUser = User.builder()
                .username("custom@test.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        // Act
        String token = jwtHelper.generateToken(customUser);
        String extractedUsername = jwtHelper.getUserNameFromToken(token);

        // Assert
        assertThat(extractedUsername).isEqualTo("custom@test.com");
    }

    @Test
    @DisplayName("Should validate token matches the user who generated it")
    void validateToken_WithOriginalUser_ShouldReturnTrue() {
        // Arrange
        UserDetails originalUser = User.builder()
                .username("original@test.com")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        String token = jwtHelper.generateToken(originalUser);

        // Act
        Boolean isValid = jwtHelper.validateToken(token, originalUser);

        // Assert
        assertThat(isValid).isTrue();
    }
}
