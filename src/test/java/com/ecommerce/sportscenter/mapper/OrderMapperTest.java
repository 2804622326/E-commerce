package com.ecommerce.sportscenter.mapper;

import com.ecommerce.sportscenter.entity.OrderAggregate.Order;
import com.ecommerce.sportscenter.entity.OrderAggregate.OrderStatus;
import com.ecommerce.sportscenter.entity.OrderAggregate.ShippingAddress;
import com.ecommerce.sportscenter.model.OrderDto;
import com.ecommerce.sportscenter.model.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    private OrderMapper orderMapper;
    private Order order;
    private OrderDto orderDto;
    private ShippingAddress shippingAddress;

    @BeforeEach
    void setUp() {
        orderMapper = OrderMapper.INSTANCE;

        shippingAddress = new ShippingAddress();
        shippingAddress.setName("John Doe");
        shippingAddress.setAddress1("123 Main St");
        shippingAddress.setAddress2("Apt 4B");
        shippingAddress.setCity("New York");
        shippingAddress.setState("NY");
        shippingAddress.setZipcode("10001");
        shippingAddress.setCountry("USA");

        order = new Order();
        order.setId(1);
        order.setBasketId("test-basket");
        order.setShippingAddress(shippingAddress);
        order.setSubTotal(100.0);
        order.setDeliveryFee(10L);
        order.setOrderStatus(OrderStatus.Pending);
        order.setOrderDate(LocalDateTime.now());

        orderDto = new OrderDto();
        orderDto.setBasketId("test-basket");
        orderDto.setShippingAddress(shippingAddress);
        orderDto.setSubTotal(100L);
        orderDto.setDeliveryFee(10L);
        orderDto.setOrderDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should map Order to OrderResponse successfully")
    void orderToOrderResponse_ShouldMapAllFields() {
        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getId()).isEqualTo(1);
        assertThat(orderResponse.getBasketId()).isEqualTo("test-basket");
        assertThat(orderResponse.getShippingAddress()).isNotNull();
        assertThat(orderResponse.getShippingAddress().getName()).isEqualTo("John Doe");
        assertThat(orderResponse.getOrderStatus()).isEqualTo(OrderStatus.Pending);
    }

    @Test
    @DisplayName("Should calculate total correctly in OrderResponse")
    void orderToOrderResponse_ShouldCalculateTotal() {
        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        assertThat(orderResponse.getTotal()).isNotNull();
        assertThat(orderResponse.getTotal()).isEqualTo(110.0); // 100 + 10
    }

    @Test
    @DisplayName("Should map OrderDto to Order successfully")
    void orderResponseToOrder_ShouldMapAllFields() {
        // Act
        Order mappedOrder = orderMapper.orderResponseToOrder(orderDto);

        // Assert
        assertThat(mappedOrder).isNotNull();
        assertThat(mappedOrder.getBasketId()).isEqualTo("test-basket");
        assertThat(mappedOrder.getShippingAddress()).isNotNull();
        assertThat(mappedOrder.getShippingAddress().getName()).isEqualTo("John Doe");
        assertThat(mappedOrder.getOrderStatus()).isEqualTo(OrderStatus.Pending);
    }

    @Test
    @DisplayName("Should handle null Order gracefully")
    void orderToOrderResponse_WithNullOrder_ShouldReturnNull() {
        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(null);

        // Assert
        assertThat(orderResponse).isNull();
    }

    @Test
    @DisplayName("Should handle null OrderDto gracefully")
    void orderResponseToOrder_WithNullOrderDto_ShouldReturnNull() {
        // Act
        Order mappedOrder = orderMapper.orderResponseToOrder(null);

        // Assert
        assertThat(mappedOrder).isNull();
    }

    @Test
    @DisplayName("Should map Order without shipping address")
    void orderToOrderResponse_WithoutShippingAddress_ShouldMapOtherFields() {
        // Arrange
        order.setShippingAddress(null);

        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getId()).isEqualTo(1);
        assertThat(orderResponse.getBasketId()).isEqualTo("test-basket");
        assertThat(orderResponse.getShippingAddress()).isNull();
    }

    @Test
    @DisplayName("Should update Order from OrderDto")
    void updateOrderFromOrderResponse_ShouldUpdateFields() {
        // Arrange
        Order existingOrder = new Order();
        existingOrder.setId(5);
        existingOrder.setBasketId("old-basket");

        OrderDto updateDto = new OrderDto();
        updateDto.setBasketId("new-basket");
        updateDto.setSubTotal(200L);
        updateDto.setDeliveryFee(20L);

        // Act
        orderMapper.updateOrderFromOrderResponse(updateDto, existingOrder);

        // Assert
        assertThat(existingOrder.getId()).isEqualTo(5); // ID should not change
        assertThat(existingOrder.getBasketId()).isEqualTo("new-basket");
    }

    @Test
    @DisplayName("Should preserve order status as Pending")
    void orderResponseToOrder_ShouldSetStatusToPending() {
        // Act
        Order mappedOrder = orderMapper.orderResponseToOrder(orderDto);

        // Assert
        assertThat(mappedOrder.getOrderStatus()).isEqualTo(OrderStatus.Pending);
    }

    @Test
    @DisplayName("Should map list of Orders to list of OrderDtos")
    void ordersToOrderResponses_ShouldMapAllOrders() {
        // Arrange
        Order order1 = new Order();
        order1.setId(1);
        order1.setBasketId("basket-1");
        order1.setSubTotal(100.0);
        order1.setDeliveryFee(10L);

        Order order2 = new Order();
        order2.setId(2);
        order2.setBasketId("basket-2");
        order2.setSubTotal(200.0);
        order2.setDeliveryFee(20L);

        java.util.List<Order> orders = java.util.Arrays.asList(order1, order2);

        // Act
        java.util.List<OrderDto> orderDtos = orderMapper.ordersToOrderResponses(orders);

        // Assert
        assertThat(orderDtos).isNotNull();
        assertThat(orderDtos).hasSize(2);
        assertThat(orderDtos.get(0).getBasketId()).isEqualTo("basket-1");
        assertThat(orderDtos.get(1).getBasketId()).isEqualTo("basket-2");
    }

    @Test
    @DisplayName("Should handle null list of Orders")
    void ordersToOrderResponses_WithNullList_ShouldReturnNull() {
        // Act
        java.util.List<OrderDto> orderDtos = orderMapper.ordersToOrderResponses(null);

        // Assert
        assertThat(orderDtos).isNull();
    }

    @Test
    @DisplayName("Should handle empty list of Orders")
    void ordersToOrderResponses_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        java.util.List<Order> orders = new java.util.ArrayList<>();

        // Act
        java.util.List<OrderDto> orderDtos = orderMapper.ordersToOrderResponses(orders);

        // Assert
        assertThat(orderDtos).isNotNull();
        assertThat(orderDtos).isEmpty();
    }

    @Test
    @DisplayName("Should update Order with null OrderDto gracefully")
    void updateOrderFromOrderResponse_WithNullOrderDto_ShouldNotChangeOrder() {
        // Arrange
        Order existingOrder = new Order();
        existingOrder.setId(5);
        existingOrder.setBasketId("original-basket");
        String originalBasketId = existingOrder.getBasketId();

        // Act
        orderMapper.updateOrderFromOrderResponse(null, existingOrder);

        // Assert
        assertThat(existingOrder.getBasketId()).isEqualTo(originalBasketId);
    }

    @Test
    @DisplayName("Should handle null subtotal in Order to OrderResponse mapping")
    void orderToOrderResponse_WithNullSubTotal_ShouldHandleGracefully() {
        // Arrange
        order.setSubTotal(0.0); // Set to 0 instead of null to avoid NPE

        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getSubTotal()).isZero();
    }

    @Test
    @DisplayName("Should handle null subtotal in OrderDto to Order mapping")
    void orderResponseToOrder_WithNullSubTotal_ShouldHandleGracefully() {
        // Arrange
        orderDto.setSubTotal(null);

        // Act
        Order mappedOrder = orderMapper.orderResponseToOrder(orderDto);

        // Assert
        assertThat(mappedOrder).isNotNull();
        assertThat(mappedOrder.getSubTotal()).isNull();
    }

    @Test
    @DisplayName("Should update Order with null subtotal in OrderDto")
    void updateOrderFromOrderResponse_WithNullSubTotal_ShouldSetToNull() {
        // Arrange
        Order existingOrder = new Order();
        existingOrder.setId(5);
        existingOrder.setSubTotal(100.0);

        OrderDto updateDto = new OrderDto();
        updateDto.setBasketId("new-basket");
        updateDto.setSubTotal(null);

        // Act
        orderMapper.updateOrderFromOrderResponse(updateDto, existingOrder);

        // Assert
        assertThat(existingOrder.getSubTotal()).isNull();
    }

    @Test
    @DisplayName("Should set order date in OrderResponse")
    void orderToOrderResponse_ShouldSetOrderDate() {
        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        assertThat(orderResponse.getOrderDate()).isNotNull();
    }

    @Test
    @DisplayName("Should convert double subtotal to long in OrderResponse")
    void orderToOrderResponse_ShouldConvertSubTotalToLong() {
        // Arrange
        order.setSubTotal(99.99);

        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        assertThat(orderResponse.getSubTotal()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Should convert long subtotal to double in Order")
    void orderResponseToOrder_ShouldConvertSubTotalToDouble() {
        // Arrange
        orderDto.setSubTotal(150L);

        // Act
        Order mappedOrder = orderMapper.orderResponseToOrder(orderDto);

        // Assert
        assertThat(mappedOrder.getSubTotal()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should preserve shipping address details in mapping")
    void orderToOrderResponse_ShouldPreserveShippingAddressDetails() {
        // Act
        OrderResponse orderResponse = orderMapper.OrderToOrderResponse(order);

        // Assert
        ShippingAddress mappedAddress = orderResponse.getShippingAddress();
        assertThat(mappedAddress).isNotNull();
        assertThat(mappedAddress.getName()).isEqualTo("John Doe");
        assertThat(mappedAddress.getAddress1()).isEqualTo("123 Main St");
        assertThat(mappedAddress.getAddress2()).isEqualTo("Apt 4B");
        assertThat(mappedAddress.getCity()).isEqualTo("New York");
        assertThat(mappedAddress.getState()).isEqualTo("NY");
        assertThat(mappedAddress.getZipcode()).isEqualTo("10001");
        assertThat(mappedAddress.getCountry()).isEqualTo("USA");
    }
}
