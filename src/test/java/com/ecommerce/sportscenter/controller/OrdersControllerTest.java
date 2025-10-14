package com.ecommerce.sportscenter.controller;

import com.ecommerce.sportscenter.config.TestSecurityConfig;
import com.ecommerce.sportscenter.entity.OrderAggregate.OrderStatus;
import com.ecommerce.sportscenter.model.OrderDto;
import com.ecommerce.sportscenter.model.OrderResponse;
import com.ecommerce.sportscenter.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(OrdersController.class)
@Import(TestSecurityConfig.class)
class OrdersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderResponse orderResponse;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        orderResponse = new OrderResponse();
        orderResponse.setId(1);
        orderResponse.setBasketId("test-basket");
        orderResponse.setOrderStatus(OrderStatus.Pending);
        orderResponse.setSubTotal(100L);
        orderResponse.setDeliveryFee(10L);

        orderDto = new OrderDto();
        orderDto.setBasketId("test-basket");
        orderDto.setSubTotal(100L);
        orderDto.setDeliveryFee(10L);
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void getOrderById_WhenOrderExists_ShouldReturnOrder() throws Exception {
        // Arrange
        when(orderService.getOrderById(1)).thenReturn(orderResponse);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.basketId").value("test-basket"))
                .andExpect(jsonPath("$.orderStatus").value("Pending"));

        verify(orderService).getOrderById(1);
    }

    @Test
    @DisplayName("Should return 404 when order not found")
    void getOrderById_WhenOrderNotFound_ShouldReturn404() throws Exception {
        // Arrange
        when(orderService.getOrderById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/orders/{id}", 999))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999);
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void getAllOrders_ShouldReturnOrderList() throws Exception {
        // Arrange
        List<OrderResponse> orders = Arrays.asList(orderResponse);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].basketId").value("test-basket"));

        verify(orderService).getAllOrders();
    }

    @Test
    @DisplayName("Should get all orders with pagination")
    void getAllOrdersPaged_ShouldReturnPagedOrders() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderResponse> orderPage = new PageImpl<>(Arrays.asList(orderResponse));
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/orders/paged")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(orderService).getAllOrders(any(Pageable.class));
    }

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_WhenValid_ShouldReturnOrderId() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(orderService).createOrder(any(OrderDto.class));
    }

    @Test
    @DisplayName("Should return 500 when order creation fails")
    void createOrder_WhenFails_ShouldReturn500() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderDto.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isInternalServerError());

        verify(orderService).createOrder(any(OrderDto.class));
    }

    @Test
    @DisplayName("Should delete order successfully")
    void deleteOrder_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(orderService).deleteOrder(1);

        // Act & Assert
        mockMvc.perform(delete("/api/orders/{id}", 1))
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(1);
    }

    @Test
    @DisplayName("Should handle empty order list")
    void getAllOrders_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(orderService).getAllOrders();
    }
}
