package com.ecommerce.sportscenter.controller;

import com.ecommerce.sportscenter.config.TestSecurityConfig;
import com.ecommerce.sportscenter.model.BrandResponse;
import com.ecommerce.sportscenter.model.ProductResponse;
import com.ecommerce.sportscenter.model.TypeResponse;
import com.ecommerce.sportscenter.service.BrandService;
import com.ecommerce.sportscenter.service.ProductService;
import com.ecommerce.sportscenter.service.TypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ProductController
 */
@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private BrandService brandService;

    @MockBean
    private TypeService typeService;

    private ProductResponse testProductResponse;
    private BrandResponse testBrandResponse;
    private TypeResponse testTypeResponse;

    @BeforeEach
    void setUp() {
        testProductResponse = ProductResponse.builder()
                .id(1)
                .name("Test Product")
                .description("Test Description")
                .price(10000L)
                .pictureUrl("/images/products/test.png")
                .productBrand("Nike")
                .productType("Running")
                .build();

        testBrandResponse = BrandResponse.builder()
                .id(1)
                .name("Nike")
                .build();

        testTypeResponse = TypeResponse.builder()
                .id(1)
                .name("Running")
                .build();
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return product successfully")
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        Integer productId = 1;
        when(productService.getProductById(productId)).thenReturn(testProductResponse);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(10000)))
                .andExpect(jsonPath("$.productBrand", is("Nike")))
                .andExpect(jsonPath("$.productType", is("Running")));
    }

    @Test
    @DisplayName("GET /api/products - Should return paginated products")
    void getProducts_ShouldReturnPaginatedProducts() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productService.getProducts(any(Pageable.class), isNull(), isNull(), isNull()))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Test Product")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.size", is(10)));
    }

    @Test
    @DisplayName("GET /api/products - Should handle pagination parameters")
    void getProducts_WithPaginationParams_ShouldReturnCorrectPage() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(1, 5), 10);
        
        when(productService.getProducts(any(Pageable.class), isNull(), isNull(), isNull()))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.totalElements", is(10)));
    }

    @Test
    @DisplayName("GET /api/products - Should filter by brandId")
    void getProducts_WithBrandIdFilter_ShouldReturnFilteredProducts() throws Exception {
        // Given
        Integer brandId = 1;
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productService.getProducts(any(Pageable.class), eq(brandId), isNull(), isNull()))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("brandId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productBrand", is("Nike")));
    }

    @Test
    @DisplayName("GET /api/products - Should filter by typeId")
    void getProducts_WithTypeIdFilter_ShouldReturnFilteredProducts() throws Exception {
        // Given
        Integer typeId = 1;
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productService.getProducts(any(Pageable.class), isNull(), eq(typeId), isNull()))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("typeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productType", is("Running")));
    }

    @Test
    @DisplayName("GET /api/products - Should filter by keyword")
    void getProducts_WithKeywordFilter_ShouldReturnFilteredProducts() throws Exception {
        // Given
        String keyword = "Test";
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productService.getProducts(any(Pageable.class), isNull(), isNull(), eq(keyword)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", containsString("Test")));
    }

    @Test
    @DisplayName("GET /api/products - Should handle sorting parameters")
    void getProducts_WithSortingParams_ShouldReturnSortedProducts() throws Exception {
        // Given
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productService.getProducts(any(Pageable.class), isNull(), isNull(), isNull()))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("sort", "price")
                        .param("order", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/products - Should combine all filters")
    void getProducts_WithAllFilters_ShouldReturnFilteredProducts() throws Exception {
        // Given
        Integer brandId = 1;
        Integer typeId = 1;
        String keyword = "Test";
        List<ProductResponse> products = Arrays.asList(testProductResponse);
        Page<ProductResponse> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        
        when(productService.getProducts(any(Pageable.class), eq(brandId), eq(typeId), eq(keyword)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("brandId", "1")
                        .param("typeId", "1")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].productBrand", is("Nike")))
                .andExpect(jsonPath("$.content[0].productType", is("Running")));
    }

    @Test
    @DisplayName("GET /api/products/brands - Should return all brands")
    void getBrands_ShouldReturnAllBrands() throws Exception {
        // Given
        List<BrandResponse> brands = Arrays.asList(testBrandResponse);
        when(brandService.getAllBrands()).thenReturn(brands);

        // When & Then
        mockMvc.perform(get("/api/products/brands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Nike")));
    }

    @Test
    @DisplayName("GET /api/products/types - Should return all types")
    void getTypes_ShouldReturnAllTypes() throws Exception {
        // Given
        List<TypeResponse> types = Arrays.asList(testTypeResponse);
        when(typeService.getAllTypes()).thenReturn(types);

        // When & Then
        mockMvc.perform(get("/api/products/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Running")));
    }

    @Test
    @DisplayName("GET /api/products - Should return empty page when no products found")
    void getProducts_WhenNoProductsFound_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<ProductResponse> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        when(productService.getProducts(any(Pageable.class), isNull(), isNull(), isNull()))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }
}
