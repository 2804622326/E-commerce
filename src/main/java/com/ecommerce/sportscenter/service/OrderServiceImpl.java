package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.OrderAggregate.Order;
import com.ecommerce.sportscenter.entity.OrderAggregate.OrderItem;
import com.ecommerce.sportscenter.entity.OrderAggregate.ProductItemOrdered;
import com.ecommerce.sportscenter.exceptions.OrderNotFoundException;
import com.ecommerce.sportscenter.mapper.OrderMapper;
import com.ecommerce.sportscenter.model.BasketItemResponse;
import com.ecommerce.sportscenter.model.BasketResponse;
import com.ecommerce.sportscenter.model.OrderDto;
import com.ecommerce.sportscenter.model.OrderResponse;
import com.ecommerce.sportscenter.repository.BrandRepository;
import com.ecommerce.sportscenter.repository.OrderRepository;
import com.ecommerce.sportscenter.repository.TypeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final BrandRepository brandRepository;
    private final TypeRepository typeRepository;
    private final BasketService basketService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, BrandRepository brandRepository, TypeRepository typeRepository, BasketService basketService, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.brandRepository = brandRepository;
        this.typeRepository = typeRepository;
        this.basketService = basketService;
        this.orderMapper = orderMapper;
    }

    @Override
    public OrderResponse getOrderById(Integer orderId) {
        log.info("Fetching Order by Id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));
        log.info("Fetched Order by Id: {}", orderId);
        return orderMapper.OrderToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(orderMapper::OrderToOrderResponse).collect(Collectors.toList());
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::OrderToOrderResponse);
    }


    @Override
    public void deleteOrder(Integer orderId) {
        log.info("Deleting Order by Id: {}", orderId);
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order with ID " + orderId + " not found");
        }
        orderRepository.deleteById(orderId);
        log.info("Deleted Order by Id: {}", orderId);
    }

    @Override
    public Integer createOrder(OrderDto orderDto) {
        log.info("Creating Order for Basket Id: {}", orderDto.getBasketId());
        // Fetching Basket details - will throw BasketNotFoundException if not found
        BasketResponse basketResponse = basketService.getBasketById(orderDto.getBasketId());
        
        // Validate basket has items
        if(basketResponse.getItems() == null || basketResponse.getItems().isEmpty()){
            log.error("Basket with ID {} has no items", orderDto.getBasketId());
            throw new IllegalArgumentException("Cannot create order: Basket is empty");
        }
        
        //Map basket items to order items
        List<OrderItem> orderItems = basketResponse.getItems().stream()
                .map(this::mapBasketItemToOrderItem)
                .collect(Collectors.toList());

        //calculate subtotal
        double subTotal = basketResponse.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        //set order details
        Order order = orderMapper.orderResponseToOrder(orderDto);
        order.setOrderItems(orderItems);
        order.setSubTotal(subTotal);

        //save the order
        Order savedOrder = orderRepository.save(order);
        basketService.deleteBasketById(orderDto.getBasketId());
        log.info("Order created with Id: {}", savedOrder.getId());
        //return the response
        return savedOrder.getId();
    }

    private OrderItem mapBasketItemToOrderItem(BasketItemResponse basketItemResponse) {
        if(basketItemResponse!=null){
            OrderItem orderItem = new OrderItem();
            orderItem.setItemOrdered(mapBasketItemToProduct(basketItemResponse));
            orderItem.setQuantity(basketItemResponse.getQuantity());
            return orderItem;
        }else{
            return null;
        }
    }

    private ProductItemOrdered mapBasketItemToProduct(BasketItemResponse basketItemResponse) {
        ProductItemOrdered productItemOrdered = new ProductItemOrdered();
        //Populate
        productItemOrdered.setName(basketItemResponse.getName());
        productItemOrdered.setPictureUrl(basketItemResponse.getPictureUrl());
        productItemOrdered.setProductId(basketItemResponse.getId());
        return productItemOrdered;
    }


}
