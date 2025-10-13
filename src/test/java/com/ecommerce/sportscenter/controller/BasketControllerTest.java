package com.ecommerce.sportscenter.controller;

import com.ecommerce.sportscenter.config.TestSecurityConfig;
import com.ecommerce.sportscenter.entity.Basket;
import com.ecommerce.sportscenter.model.BasketItemResponse;
import com.ecommerce.sportscenter.model.BasketResponse;
import com.ecommerce.sportscenter.service.BasketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BasketController
 */
@WebMvcTest(BasketController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Basket Controller Integration Tests")
class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BasketService basketService;

    private BasketResponse testBasketResponse;
    private BasketItemResponse testBasketItemResponse;

    @BeforeEach
    void setUp() {
        testBasketItemResponse = BasketItemResponse.builder()
                .id(1)
                .name("Test Product")
                .description("Test Description")
                .price(10000L)
                .pictureUrl("/images/products/test.png")
                .productBrand("Nike")
                .productType("Running")
                .quantity(2)
                .build();

        testBasketResponse = BasketResponse.builder()
                .id("test-basket-id")
                .items(Arrays.asList(testBasketItemResponse))
                .build();
    }

    @Test
    @DisplayName("GET /api/baskets - Should return all baskets")
    void getAllBaskets_ShouldReturnAllBaskets() throws Exception {
        // Given
        List<BasketResponse> baskets = Arrays.asList(testBasketResponse);
        when(basketService.getAllBaskets()).thenReturn(baskets);

        // When & Then
        mockMvc.perform(get("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("test-basket-id")))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Test Product")));
    }

    @Test
    @DisplayName("GET /api/baskets/{basketId} - Should return basket by ID")
    void getBasketById_WhenBasketExists_ShouldReturnBasket() throws Exception {
        // Given
        String basketId = "test-basket-id";
        when(basketService.getBasketById(basketId)).thenReturn(testBasketResponse);

        // When & Then
        mockMvc.perform(get("/api/baskets/{basketId}", basketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(basketId)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", is("Test Product")))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }

    @Test
    @DisplayName("GET /api/baskets/{basketId} - Should return null when basket not found")
    void getBasketById_WhenBasketDoesNotExist_ShouldReturnNull() throws Exception {
        // Given
        String basketId = "non-existent-basket";
        when(basketService.getBasketById(basketId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/baskets/{basketId}", basketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("DELETE /api/baskets/{basketId} - Should delete basket successfully")
    void deleteBasketById_ShouldReturnOk() throws Exception {
        // Given
        String basketId = "test-basket-id";
        doNothing().when(basketService).deleteBasketById(basketId);

        // When & Then
        mockMvc.perform(delete("/api/baskets/{basketId}", basketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(basketService, times(1)).deleteBasketById(basketId);
    }

    @Test
    @DisplayName("POST /api/baskets - Should create basket successfully")
    void createBasket_ShouldReturnCreatedBasket() throws Exception {
        // Given
        when(basketService.createBasket(any(Basket.class))).thenReturn(testBasketResponse);

        // When & Then
        mockMvc.perform(post("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBasketResponse)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("test-basket-id")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", is("Test Product")));

        verify(basketService, times(1)).createBasket(any(Basket.class));
    }

    @Test
    @DisplayName("POST /api/baskets - Should create basket with empty items")
    void createBasket_WithEmptyItems_ShouldReturnCreatedBasket() throws Exception {
        // Given
        BasketResponse emptyBasket = BasketResponse.builder()
                .id("empty-basket-id")
                .items(Arrays.asList())
                .build();
        when(basketService.createBasket(any(Basket.class))).thenReturn(emptyBasket);

        // When & Then
        mockMvc.perform(post("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyBasket)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("empty-basket-id")))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/baskets - Should create basket with multiple items")
    void createBasket_WithMultipleItems_ShouldReturnCreatedBasket() throws Exception {
        // Given
        BasketItemResponse secondItem = BasketItemResponse.builder()
                .id(2)
                .name("Second Product")
                .quantity(3)
                .build();
        
        BasketResponse multiItemBasket = BasketResponse.builder()
                .id("multi-item-basket")
                .items(Arrays.asList(testBasketItemResponse, secondItem))
                .build();
        
        when(basketService.createBasket(any(Basket.class))).thenReturn(multiItemBasket);

        // When & Then
        mockMvc.perform(post("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(multiItemBasket)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("multi-item-basket")))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].name", is("Test Product")))
                .andExpect(jsonPath("$.items[1].name", is("Second Product")));
    }

    @Test
    @DisplayName("GET /api/baskets - Should return empty list when no baskets exist")
    void getAllBaskets_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(basketService.getAllBaskets()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/baskets - Should handle basket item with all properties")
    void createBasket_WithAllItemProperties_ShouldPreserveAllData() throws Exception {
        // Given
        when(basketService.createBasket(any(Basket.class))).thenReturn(testBasketResponse);

        // When & Then
        mockMvc.perform(post("/api/baskets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBasketResponse)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Test Product")))
                .andExpect(jsonPath("$.items[0].description", is("Test Description")))
                .andExpect(jsonPath("$.items[0].price", is(10000)))
                .andExpect(jsonPath("$.items[0].pictureUrl", is("/images/products/test.png")))
                .andExpect(jsonPath("$.items[0].productBrand", is("Nike")))
                .andExpect(jsonPath("$.items[0].productType", is("Running")))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }
}
