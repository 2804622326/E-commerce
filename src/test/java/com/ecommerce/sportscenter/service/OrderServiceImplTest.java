package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.OrderAggregate.Order;
import com.ecommerce.sportscenter.entity.OrderAggregate.OrderStatus;
import com.ecommerce.sportscenter.exceptions.BasketNotFoundException;
import com.ecommerce.sportscenter.exceptions.OrderNotFoundException;
import com.ecommerce.sportscenter.mapper.OrderMapper;
import com.ecommerce.sportscenter.model.BasketItemResponse;
import com.ecommerce.sportscenter.model.BasketResponse;
import com.ecommerce.sportscenter.model.OrderDto;
import com.ecommerce.sportscenter.model.OrderResponse;
import com.ecommerce.sportscenter.repository.BrandRepository;
import com.ecommerce.sportscenter.repository.OrderRepository;
import com.ecommerce.sportscenter.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private BasketService basketService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderResponse orderResponse;
    private OrderDto orderDto;
    private BasketResponse basketResponse;

    @BeforeEach
    void setUp() {
        // Setup Order
        order = new Order();
        order.setId(1);
        order.setBasketId("test-basket");
        order.setOrderStatus(OrderStatus.Pending);
        order.setSubTotal(100.0);
        order.setDeliveryFee(10L);

        // Setup OrderResponse
        orderResponse = new OrderResponse();
        orderResponse.setId(1);
        orderResponse.setBasketId("test-basket");
        orderResponse.setOrderStatus(OrderStatus.Pending);
        orderResponse.setSubTotal(100L);
        orderResponse.setDeliveryFee(10L);

        // Setup OrderDto
        orderDto = new OrderDto();
        orderDto.setBasketId("test-basket");
        orderDto.setSubTotal(100L);
        orderDto.setDeliveryFee(10L);

        // Setup BasketResponse
        BasketItemResponse item1 = new BasketItemResponse();
        item1.setId(1);
        item1.setName("Product 1");
        item1.setPrice(50L);
        item1.setQuantity(2);
        item1.setPictureUrl("url1");

        basketResponse = new BasketResponse();
        basketResponse.setId("test-basket");
        basketResponse.setItems(Arrays.asList(item1));
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        // Arrange
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderMapper.OrderToOrderResponse(order)).thenReturn(orderResponse);

        // Act
        OrderResponse result = orderService.getOrderById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getBasketId()).isEqualTo("test-basket");
        verify(orderRepository).findById(1);
        verify(orderMapper).OrderToOrderResponse(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.getOrderById(999))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order with ID 999 not found");
        
        verify(orderRepository).findById(999);
        verify(orderMapper, never()).OrderToOrderResponse(any());
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void getAllOrders_ShouldReturnOrderList() {
        // Arrange
        Order order2 = new Order();
        order2.setId(2);
        order2.setBasketId("test-basket-2");

        OrderResponse orderResponse2 = new OrderResponse();
        orderResponse2.setId(2);
        orderResponse2.setBasketId("test-basket-2");

        List<Order> orders = Arrays.asList(order, order2);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.OrderToOrderResponse(order)).thenReturn(orderResponse);
        when(orderMapper.OrderToOrderResponse(order2)).thenReturn(orderResponse2);

        // Act
        List<OrderResponse> result = orderService.getAllOrders();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(2);
        verify(orderRepository).findAll();
        verify(orderMapper, times(2)).OrderToOrderResponse(any(Order.class));
    }

    @Test
    @DisplayName("Should get all orders with pagination")
    void getAllOrdersWithPagination_ShouldReturnPagedOrders() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(order));
        Page<OrderResponse> orderResponsePage = new PageImpl<>(Arrays.asList(orderResponse));

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.OrderToOrderResponse(any(Order.class))).thenReturn(orderResponse);

        // Act
        Page<OrderResponse> result = orderService.getAllOrders(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(orderRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should delete order successfully")
    void deleteOrder_ShouldCallRepository() {
        // Arrange
        when(orderRepository.existsById(1)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1);

        // Act
        orderService.deleteOrder(1);

        // Assert
        verify(orderRepository).existsById(1);
        verify(orderRepository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when deleting non-existent order")
    void deleteOrder_ShouldThrowException_WhenOrderNotFound() {
        // Arrange
        when(orderRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> orderService.deleteOrder(999))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order with ID 999 not found");
        
        verify(orderRepository).existsById(999);
        verify(orderRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_ShouldReturnOrderId_WhenBasketExists() {
        // Arrange
        when(basketService.getBasketById("test-basket")).thenReturn(basketResponse);
        when(orderMapper.orderResponseToOrder(any(OrderDto.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(basketService).deleteBasketById("test-basket");

        // Act
        Integer result = orderService.createOrder(orderDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);
        verify(basketService).getBasketById("test-basket");
        verify(orderRepository).save(any(Order.class));
        verify(basketService).deleteBasketById("test-basket");
    }

    @Test
    @DisplayName("Should throw BasketNotFoundException when basket not found during order creation")
    void createOrder_ShouldThrowException_WhenBasketNotFound() {
        // Arrange
        OrderDto nonExistentOrderDto = new OrderDto();
        nonExistentOrderDto.setBasketId("non-existent-basket");
        when(basketService.getBasketById("non-existent-basket"))
            .thenThrow(new BasketNotFoundException("Basket with ID non-existent-basket not found"));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(nonExistentOrderDto))
            .isInstanceOf(BasketNotFoundException.class)
            .hasMessageContaining("Basket with ID non-existent-basket not found");
        
        verify(basketService).getBasketById("non-existent-basket");
        verify(orderRepository, never()).save(any());
        verify(basketService, never()).deleteBasketById(anyString());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when basket is empty")
    void createOrder_ShouldThrowException_WhenBasketIsEmpty() {
        // Arrange
        BasketResponse emptyBasket = new BasketResponse();
        emptyBasket.setId("empty-basket");
        emptyBasket.setItems(new ArrayList<>());

        OrderDto emptyOrderDto = new OrderDto();
        emptyOrderDto.setBasketId("empty-basket");
        emptyOrderDto.setSubTotal(0L);

        when(basketService.getBasketById("empty-basket")).thenReturn(emptyBasket);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(emptyOrderDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Basket is empty");
        
        verify(basketService).getBasketById("empty-basket");
        verify(orderRepository, never()).save(any(Order.class));
        verify(basketService, never()).deleteBasketById(anyString());
    }
}
