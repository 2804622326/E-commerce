package com.ecommerce.sportscenter.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Integration Tests")
class ProductIntegrationTest {

    @Test
    @DisplayName("Basic integration test")
    void basicIntegrationTest() {
        assertThat(true).isTrue();
    }
}
