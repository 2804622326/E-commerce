package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.Type;
import com.ecommerce.sportscenter.model.TypeResponse;
import com.ecommerce.sportscenter.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Type Service Unit Tests")
class TypeServiceImplTest {

    @Mock
    private TypeRepository typeRepository;

    @InjectMocks
    private TypeServiceImpl typeService;

    private Type testType;

    @BeforeEach
    void setUp() {
        testType = Type.builder()
                .id(1)
                .name("Running")
                .build();
    }

    @Test
    @DisplayName("Should return all types")
    void getTypes_ShouldReturnAllTypes() {
        // Given
        List<Type> types = Arrays.asList(testType);
        when(typeRepository.findAll()).thenReturn(types);

        // When
        List<TypeResponse> result = typeService.getAllTypes();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Running");

        verify(typeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no types exist")
    void getTypes_WhenNoTypes_ShouldReturnEmptyList() {
        // Given
        when(typeRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<TypeResponse> result = typeService.getAllTypes();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(typeRepository, times(1)).findAll();
    }
}
