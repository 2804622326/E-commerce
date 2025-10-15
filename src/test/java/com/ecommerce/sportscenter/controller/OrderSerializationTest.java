package com.ecommerce.sportscenter.controller;

import com.ecommerce.sportscenter.entity.OrderAggregate.OrderStatus;
import com.ecommerce.sportscenter.model.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class OrderSerializationTest {

    @Test
    void testOrderResponseSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Register JSR310 module for LocalDateTime
        
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(1);
        orderResponse.setBasketId("test-basket");
        orderResponse.setOrderStatus(OrderStatus.Pending);
        orderResponse.setSubTotal(100L);
        orderResponse.setDeliveryFee(10L);
        orderResponse.setTotal(110.0);
        orderResponse.setOrderDate(LocalDateTime.of(2024, 1, 1, 10, 0));

        // This should not throw an exception
        String json = objectMapper.writeValueAsString(orderResponse);
        System.out.println("Serialized JSON: " + json);
    }
}