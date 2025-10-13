package com.ecommerce.sportscenter.exceptions;

import com.ecommerce.sportscenter.model.CustomErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CustomExceptionHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Exception Handler Tests")
class CustomExceptionHandlerTest {

    @InjectMocks
    private CustomExceptionHandler exceptionHandler;

    @Test
    @DisplayName("Should handle ProductNotFoundException correctly")
    void handleProductNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Product not found";
        ProductNotFoundException exception = new ProductNotFoundException(errorMessage);

        // When
        ResponseEntity<CustomErrorResponse> response = exceptionHandler.handleProductNotFoundException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Given
        String errorMessage = "Internal server error";
        Exception exception = new Exception(errorMessage);

        // When
        ResponseEntity<CustomErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(errorMessage);
        assertThat(response.getBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should preserve exception message in response")
    void handleException_ShouldPreserveExceptionMessage() {
        // Given
        String customMessage = "Custom error message";
        ProductNotFoundException exception = new ProductNotFoundException(customMessage);

        // When
        ResponseEntity<CustomErrorResponse> response = exceptionHandler.handleProductNotFoundException(exception);

        // Then
        assertThat(response.getBody().getErrorMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void handleException_ShouldIncludeTimestamp() {
        // Given
        LocalDateTime beforeHandling = LocalDateTime.now();
        ProductNotFoundException exception = new ProductNotFoundException("Error");

        // When
        ResponseEntity<CustomErrorResponse> response = exceptionHandler.handleProductNotFoundException(exception);

        // Then
        LocalDateTime afterHandling = LocalDateTime.now();
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isBetween(beforeHandling.minusSeconds(1), afterHandling.plusSeconds(1));
    }
}
