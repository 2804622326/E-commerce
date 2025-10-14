package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.Brand;
import com.ecommerce.sportscenter.model.BrandResponse;
import com.ecommerce.sportscenter.repository.BrandRepository;
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
@DisplayName("Brand Service Unit Tests")
class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = Brand.builder()
                .id(1)
                .name("Nike")
                .build();
    }

    @Test
    @DisplayName("Should return all brands")
    void getBrands_ShouldReturnAllBrands() {
        // Given
        List<Brand> brands = Arrays.asList(testBrand);
        when(brandRepository.findAll()).thenReturn(brands);

        // When
        List<BrandResponse> result = brandService.getAllBrands();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Nike");

        verify(brandRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no brands exist")
    void getBrands_WhenNoBrands_ShouldReturnEmptyList() {
        // Given
        when(brandRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<BrandResponse> result = brandService.getAllBrands();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(brandRepository, times(1)).findAll();
    }
}
