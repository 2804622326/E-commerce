package com.ecommerce.sportscenter.exceptions;

import com.ecommerce.sportscenter.model.CustomErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for CustomExceptionHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Exception Handler Tests")
class CustomExceptionHandlerTest {

    @InjectMocks
    private CustomExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Test
    @DisplayName("Should handle ProductNotFoundException correctly")
    void handleProductNotFoundException_ShouldReturnNotFoundResponse() {
        // Given
        String errorMessage = "Product not found";
        ProductNotFoundException exception = new ProductNotFoundException(errorMessage);
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleProductNotFoundException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(CustomErrorResponse.class);
        
        CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo(errorMessage);
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorResponse.getError()).isEqualTo("Product Not Found");
    }

    @Test
    @DisplayName("Should preserve exception message in response")
    void handleException_ShouldPreserveExceptionMessage() {
        // Given
        String customMessage = "Custom error message";
        ProductNotFoundException exception = new ProductNotFoundException(customMessage);
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleProductNotFoundException(exception, request);

        // Then
        CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("Should return correct HTTP status")
    void handleException_ShouldReturnCorrectStatus() {
        // Given
        ProductNotFoundException exception = new ProductNotFoundException("Error");
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleProductNotFoundException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with BAD_REQUEST status")
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        // Given
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleIllegalArgumentException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(CustomErrorResponse.class);
        
        CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo(errorMessage);
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getError()).isEqualTo("Invalid Argument");
    }

    @Test
    @DisplayName("Should handle generic Exception with INTERNAL_SERVER_ERROR status")
    void handleGlobalException_ShouldReturnInternalServerError() {
        // Given
        String errorMessage = "Unexpected error occurred";
        Exception exception = new Exception(errorMessage);
        WebRequest request = mock(WebRequest.class);

        // When
        ResponseEntity<Object> response = exceptionHandler.handleGlobalException(exception, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(CustomErrorResponse.class);
        
        CustomErrorResponse errorResponse = (CustomErrorResponse) response.getBody();
        assertThat(errorResponse.getMessage()).isEqualTo(errorMessage);
        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
    }
}
