package com.ecommerce.sportscenter.integration;

import com.ecommerce.sportscenter.entity.Brand;
import com.ecommerce.sportscenter.entity.Product;
import com.ecommerce.sportscenter.entity.Type;
import com.ecommerce.sportscenter.repository.BrandRepository;
import com.ecommerce.sportscenter.repository.ProductRepository;
import com.ecommerce.sportscenter.repository.TypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full integration tests for Product API
 * Tests the entire stack from controller to database
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Product API Integration Tests")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TypeRepository typeRepository;

    private Brand nikeBrand;
    private Brand adidasBrand;
    private Type runningType;
    private Type basketballType;

    @BeforeEach
    void setUp() {
        // Clear existing data
        productRepository.deleteAll();
        brandRepository.deleteAll();
        typeRepository.deleteAll();

        // Create test brands
        nikeBrand = brandRepository.save(Brand.builder().name("Nike").build());
        adidasBrand = brandRepository.save(Brand.builder().name("Adidas").build());

        // Create test types
        runningType = typeRepository.save(Type.builder().name("Running").build());
        basketballType = typeRepository.save(Type.builder().name("Basketball").build());

        // Create test products
        productRepository.save(Product.builder()
                .name("Nike Air Max")
                .description("Running shoes")
                .price(15000L)
                .pictureUrl("/images/nike-air-max.png")
                .brand(nikeBrand)
                .type(runningType)
                .build());

        productRepository.save(Product.builder()
                .name("Nike Jordan")
                .description("Basketball shoes")
                .price(20000L)
                .pictureUrl("/images/nike-jordan.png")
                .brand(nikeBrand)
                .type(basketballType)
                .build());

        productRepository.save(Product.builder()
                .name("Adidas Ultraboost")
                .description("Premium running shoes")
                .price(18000L)
                .pictureUrl("/images/adidas-ultraboost.png")
                .brand(adidasBrand)
                .type(runningType)
                .build());
    }

    @Test
    @DisplayName("GET /api/products - Should return all products with pagination")
    void getAllProducts_ShouldReturnPaginatedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @DisplayName("GET /api/products - Should filter by brand")
    void getProducts_FilterByBrand_ShouldReturnNikeProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("brandId", String.valueOf(nikeBrand.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].productBrand", everyItem(is("Nike"))));
    }

    @Test
    @DisplayName("GET /api/products - Should filter by type")
    void getProducts_FilterByType_ShouldReturnRunningProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("typeId", String.valueOf(runningType.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].productType", everyItem(is("Running"))));
    }

    @Test
    @DisplayName("GET /api/products - Should filter by keyword")
    void getProducts_FilterByKeyword_ShouldReturnMatchingProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("keyword", "Nike")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", everyItem(containsString("Nike"))));
    }

    @Test
    @DisplayName("GET /api/products - Should combine brand and type filters")
    void getProducts_CombineFilters_ShouldReturnFilteredProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("brandId", String.valueOf(nikeBrand.getId()))
                        .param("typeId", String.valueOf(runningType.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Nike Air Max")))
                .andExpect(jsonPath("$.content[0].productBrand", is("Nike")))
                .andExpect(jsonPath("$.content[0].productType", is("Running")));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return specific product")
    void getProductById_ShouldReturnProduct() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(get("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId())))
                .andExpect(jsonPath("$.name", is(product.getName())))
                .andExpect(jsonPath("$.description", is(product.getDescription())))
                .andExpect(jsonPath("$.price", is(product.getPrice().intValue())));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 for non-existent product")
    void getProductById_NonExistent_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products - Should handle pagination correctly")
    void getProducts_WithPagination_ShouldReturnCorrectPage() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(2)));
    }

    @Test
    @DisplayName("GET /api/products - Should sort by name ascending")
    void getProducts_SortByNameAsc_ShouldReturnSortedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("sort", "name")
                        .param("order", "asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Adidas Ultraboost")))
                .andExpect(jsonPath("$.content[1].name", is("Nike Air Max")))
                .andExpect(jsonPath("$.content[2].name", is("Nike Jordan")));
    }

    @Test
    @DisplayName("GET /api/products - Should sort by price descending")
    void getProducts_SortByPriceDesc_ShouldReturnSortedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("sort", "price")
                        .param("order", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].price", is(20000)))
                .andExpect(jsonPath("$.content[1].price", is(18000)))
                .andExpect(jsonPath("$.content[2].price", is(15000)));
    }

    @Test
    @DisplayName("GET /api/products/brands - Should return all brands")
    void getBrands_ShouldReturnAllBrands() throws Exception {
        mockMvc.perform(get("/api/products/brands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Nike", "Adidas")));
    }

    @Test
    @DisplayName("GET /api/products/types - Should return all types")
    void getTypes_ShouldReturnAllTypes() throws Exception {
        mockMvc.perform(get("/api/products/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Running", "Basketball")));
    }

    @Test
    @DisplayName("GET /api/products - Should return empty result for non-matching filter")
    void getProducts_NoMatches_ShouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("keyword", "NonExistentProduct")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("GET /api/products - Should handle multiple keywords in search")
    void getProducts_MultipleKeywords_ShouldSearchCorrectly() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("keyword", "Air")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", containsString("Air")));
    }
}
