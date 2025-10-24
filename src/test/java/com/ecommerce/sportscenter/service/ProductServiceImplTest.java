package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.Brand;
import com.ecommerce.sportscenter.entity.Product;
import com.ecommerce.sportscenter.entity.Type;
import com.ecommerce.sportscenter.exceptions.ProductNotFoundException;
import com.ecommerce.sportscenter.model.ProductResponse;
import com.ecommerce.sportscenter.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductServiceImpl
 * Tests all business logic without Spring context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Brand testBrand;
    private Type testType;

    @BeforeEach
    void setUp() {
        // Setup test data
        testBrand = Brand.builder()
                .id(1)
                .name("Nike")
                .build();

        testType = Type.builder()
                .id(1)
                .name("Running")
                .build();

        testProduct = Product.builder()
                .id(1)
                .name("Test Product")
                .description("Test Description")
                .price(10000L)
                .pictureUrl("/images/products/test.png")
                .brand(testBrand)
                .type(testType)
                .build();
    }

    @Test
    @DisplayName("Should return product by ID successfully")
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        Integer productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse result = productService.getProductById(productId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getPrice()).isEqualTo(10000L);
        assertThat(result.getProductBrand()).isEqualTo("Nike");
        assertThat(result.getProductType()).isEqualTo("Running");

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product does not exist")
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Given
        Integer productId = 999;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product doesn't exist");

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should return all products with pagination")
    void getProducts_WithNullFilters_ShouldReturnAllProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, null, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should filter products by brand ID")
    void getProducts_WithBrandIdFilter_ShouldReturnFilteredProducts() {
        // Given
        Integer brandId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, brandId, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProductBrand()).isEqualTo("Nike");

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should filter products by type ID")
    void getProducts_WithTypeIdFilter_ShouldReturnFilteredProducts() {
        // Given
        Integer typeId = 1;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, null, typeId, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProductType()).isEqualTo("Running");

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should filter products by keyword")
    void getProducts_WithKeywordFilter_ShouldReturnFilteredProducts() {
        // Given
        String keyword = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, null, null, keyword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("Test");

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should filter products with all filters combined")
    void getProducts_WithAllFilters_ShouldReturnFilteredProducts() {
        // Given
        Integer brandId = 1;
        Integer typeId = 1;
        String keyword = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, brandId, typeId, keyword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("Test");
        assertThat(result.getContent().get(0).getProductBrand()).isEqualTo("Nike");
        assertThat(result.getContent().get(0).getProductType()).isEqualTo("Running");

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty page when no products match filters")
    void getProducts_WithNoMatches_ShouldReturnEmptyPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, 999, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should handle empty keyword filter correctly")
    void getProducts_WithEmptyKeyword_ShouldIgnoreKeywordFilter() {
        // Given
        String emptyKeyword = "";
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.getProducts(pageable, null, null, emptyKeyword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
