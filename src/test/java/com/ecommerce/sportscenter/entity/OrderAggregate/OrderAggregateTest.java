package com.ecommerce.sportscenter.entity.OrderAggregate;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for OrderAggregate entities
 * Tests complex business logic and all Lombok generated methods
 */
class OrderAggregateTest {

    @Test
    void testOrderStatus_AllValues() {
        assertEquals(3, OrderStatus.values().length);
        
        assertEquals(OrderStatus.Pending, OrderStatus.valueOf("Pending"));
        assertEquals(OrderStatus.PaymentReceived, OrderStatus.valueOf("PaymentReceived"));
        assertEquals(OrderStatus.PaymentFailed, OrderStatus.valueOf("PaymentFailed"));
        
        assertNotNull(OrderStatus.Pending.toString());
        assertEquals("Pending", OrderStatus.Pending.name());
    }

    @Test
    void testShippingAddress_AllMethods() {
        ShippingAddress address = ShippingAddress.builder()
                .name("John Doe")
                .address1("123 Main Street")
                .address2("Apartment 4B")
                .city("New York")
                .state("NY")
                .zipcode("10001")
                .country("USA")
                .build();
        
        assertEquals("John Doe", address.getName());
        assertEquals("123 Main Street", address.getAddress1());
        assertEquals("Apartment 4B", address.getAddress2());
        assertEquals("New York", address.getCity());
        assertEquals("NY", address.getState());
        assertEquals("10001", address.getZipcode());
        assertEquals("USA", address.getCountry());
        
        // Test setters
        address.setName("Jane Smith");
        address.setAddress1("456 Oak Avenue");
        address.setAddress2("Suite 200");
        address.setCity("Boston");
        address.setState("MA");
        address.setZipcode("02101");
        address.setCountry("United States");
        
        assertEquals("Jane Smith", address.getName());
        assertEquals("456 Oak Avenue", address.getAddress1());
        assertEquals("Suite 200", address.getAddress2());
        assertEquals("Boston", address.getCity());
        assertEquals("MA", address.getState());
        assertEquals("02101", address.getZipcode());
        assertEquals("United States", address.getCountry());
        
        // Test constructors
        ShippingAddress emptyAddress = new ShippingAddress();
        assertNull(emptyAddress.getName());
        
        ShippingAddress fullAddress = new ShippingAddress(
                "Test", "Addr1", "Addr2", "City", "State", "Zip", "Country"
        );
        assertEquals("Test", fullAddress.getName());
        
        assertNotNull(address.toString());
        assertTrue(address.toString().contains("Jane Smith"));
        
        // Test equals and hashCode
        ShippingAddress addr1 = new ShippingAddress("Name", "A1", "A2", "C", "S", "Z", "Country");
        ShippingAddress addr2 = new ShippingAddress("Name", "A1", "A2", "C", "S", "Z", "Country");
        
        assertEquals(addr1, addr2);
        assertEquals(addr1.hashCode(), addr2.hashCode());
    }

    @Test
    void testProductItemOrdered_AllMethods() {
        ProductItemOrdered item = ProductItemOrdered.builder()
                .productId(1)
                .name("Nike Running Shoes")
                .pictureUrl("/images/nike-shoes.jpg")
                .build();
        
        assertEquals(1, item.getProductId());
        assertEquals("Nike Running Shoes", item.getName());
        assertEquals("/images/nike-shoes.jpg", item.getPictureUrl());
        
        item.setProductId(2);
        item.setName("Adidas T-Shirt");
        item.setPictureUrl("/images/adidas-tshirt.jpg");
        
        assertEquals(2, item.getProductId());
        assertEquals("Adidas T-Shirt", item.getName());
        assertEquals("/images/adidas-tshirt.jpg", item.getPictureUrl());
        
        ProductItemOrdered emptyItem = new ProductItemOrdered();
        assertNull(emptyItem.getProductId());
        
        ProductItemOrdered fullItem = new ProductItemOrdered(3, "Product", "/pic.jpg");
        assertEquals(3, fullItem.getProductId());
        assertEquals("Product", fullItem.getName());
        
        assertNotNull(item.toString());
        assertTrue(item.toString().contains("Adidas T-Shirt"));
        
        ProductItemOrdered item1 = new ProductItemOrdered(1, "Item", "/pic");
        ProductItemOrdered item2 = new ProductItemOrdered(1, "Item", "/pic");
        
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testOrderItem_AllMethods() {
        ProductItemOrdered productItem = ProductItemOrdered.builder()
                .productId(1)
                .name("Product")
                .pictureUrl("/pic.jpg")
                .build();
        
        OrderItem orderItem = OrderItem.builder()
                .id(1)
                .itemOrdered(productItem)
                .price(5000L)
                .quantity(2)
                .build();
        
        assertEquals(1, orderItem.getId());
        assertEquals(productItem, orderItem.getItemOrdered());
        assertEquals(5000L, orderItem.getPrice());
        assertEquals(2, orderItem.getQuantity());
        
        orderItem.setId(2);
        orderItem.setPrice(6000L);
        orderItem.setQuantity(3);
        
        assertEquals(2, orderItem.getId());
        assertEquals(6000L, orderItem.getPrice());
        assertEquals(3, orderItem.getQuantity());
        
        OrderItem emptyItem = new OrderItem();
        assertNull(emptyItem.getId());
        
        Order order = new Order();
        orderItem.setOrder(order);
        assertEquals(order, orderItem.getOrder());
        
        assertNotNull(orderItem.toString());
    }

    @Test
    void testOrderItem_WithOrder() {
        Order order = Order.builder()
                .id(1)
                .basketId("basket-123")
                .build();
        
        ProductItemOrdered productItem = new ProductItemOrdered(1, "Item", "/pic");
        
        OrderItem orderItem = OrderItem.builder()
                .itemOrdered(productItem)
                .price(1000L)
                .quantity(1)
                .order(order)
                .build();
        
        assertEquals(order, orderItem.getOrder());
        assertEquals("basket-123", orderItem.getOrder().getBasketId());
    }

    @Test
    void testOrder_AllMethods() {
        ShippingAddress address = ShippingAddress.builder()
                .name("Customer")
                .address1("Address")
                .city("City")
                .state("State")
                .zipcode("12345")
                .country("Country")
                .build();
        
        LocalDateTime orderDate = LocalDateTime.of(2024, 3, 15, 10, 30);
        
        Order order = Order.builder()
                .id(1)
                .basketId("basket-abc")
                .shippingAddress(address)
                .orderDate(orderDate)
                .subTotal(10000.0)
                .deliveryFee(500L)
                .orderStatus(OrderStatus.Pending)
                .build();
        
        assertEquals(1, order.getId());
        assertEquals("basket-abc", order.getBasketId());
        assertEquals(address, order.getShippingAddress());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(10000.0, order.getSubTotal());
        assertEquals(500L, order.getDeliveryFee());
        assertEquals(OrderStatus.Pending, order.getOrderStatus());
        
        // Test getTotal() business logic
        assertEquals(10500.0, order.getTotal());
        
        // Test setters
        order.setId(2);
        order.setBasketId("basket-xyz");
        order.setSubTotal(20000.0);
        order.setDeliveryFee(1000L);
        order.setOrderStatus(OrderStatus.PaymentReceived);
        
        assertEquals(2, order.getId());
        assertEquals("basket-xyz", order.getBasketId());
        assertEquals(20000.0, order.getSubTotal());
        assertEquals(1000L, order.getDeliveryFee());
        assertEquals(OrderStatus.PaymentReceived, order.getOrderStatus());
        assertEquals(21000.0, order.getTotal());
        
        Order emptyOrder = new Order();
        assertNull(emptyOrder.getId());
        assertNotNull(emptyOrder.getOrderDate()); // Default to now
        assertEquals(OrderStatus.Pending, emptyOrder.getOrderStatus()); // Default
        
        assertNotNull(order.toString());
        assertTrue(order.toString().contains("basket-xyz"));
    }

    @Test
    void testOrder_WithOrderItems() {
        ShippingAddress address = new ShippingAddress(
                "John", "123 St", null, "NYC", "NY", "10001", "USA"
        );
        
        Order order = Order.builder()
                .basketId("basket-001")
                .shippingAddress(address)
                .subTotal(15000.0)
                .deliveryFee(750L)
                .orderStatus(OrderStatus.Pending)
                .build();
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            ProductItemOrdered productItem = ProductItemOrdered.builder()
                    .productId(i)
                    .name("Product " + i)
                    .pictureUrl("/pic" + i + ".jpg")
                    .build();
            
            OrderItem orderItem = OrderItem.builder()
                    .itemOrdered(productItem)
                    .price(5000L)
                    .quantity(i)
                    .order(order)
                    .build();
            
            orderItems.add(orderItem);
        }
        
        order.setOrderItems(orderItems);
        
        assertEquals(3, order.getOrderItems().size());
        assertEquals("Product 1", order.getOrderItems().get(0).getItemOrdered().getName());
        assertEquals("Product 3", order.getOrderItems().get(2).getItemOrdered().getName());
        assertEquals(3, order.getOrderItems().get(2).getQuantity());
        
        // Test getTotal with items
        assertEquals(15750.0, order.getTotal());
    }

    @Test
    void testOrder_TotalCalculation() {
        Order order1 = Order.builder()
                .subTotal(1000.0)
                .deliveryFee(100L)
                .build();
        assertEquals(1100.0, order1.getTotal());
        
        Order order2 = Order.builder()
                .subTotal(0.0)
                .deliveryFee(0L)
                .build();
        assertEquals(0.0, order2.getTotal());
        
        Order order3 = Order.builder()
                .subTotal(99999.99)
                .deliveryFee(9999L)
                .build();
        assertEquals(109998.99, order3.getTotal(), 0.01);
    }

    @Test
    void testOrder_DefaultValues() {
        Order order = new Order();
        
        assertNotNull(order.getOrderDate());
        assertEquals(OrderStatus.Pending, order.getOrderStatus());
        
        LocalDateTime now = LocalDateTime.now();
        assertTrue(order.getOrderDate().isBefore(now.plusSeconds(1)));
        assertTrue(order.getOrderDate().isAfter(now.minusSeconds(5)));
    }

    @Test
    void testOrder_StatusTransitions() {
        Order order = Order.builder()
                .basketId("basket")
                .subTotal(1000.0)
                .deliveryFee(100L)
                .orderStatus(OrderStatus.Pending)
                .build();
        
        assertEquals(OrderStatus.Pending, order.getOrderStatus());
        
        order.setOrderStatus(OrderStatus.PaymentReceived);
        assertEquals(OrderStatus.PaymentReceived, order.getOrderStatus());
        
        order.setOrderStatus(OrderStatus.PaymentFailed);
        assertEquals(OrderStatus.PaymentFailed, order.getOrderStatus());
    }

    @Test
    void testOrder_ComplexScenario() {
        // Create a complete order with all relationships
        ShippingAddress address = ShippingAddress.builder()
                .name("Alice Johnson")
                .address1("789 Pine Street")
                .address2("Floor 3")
                .city("Seattle")
                .state("WA")
                .zipcode("98101")
                .country("USA")
                .build();
        
        Order order = Order.builder()
                .basketId("basket-premium-001")
                .shippingAddress(address)
                .orderDate(LocalDateTime.of(2024, 6, 1, 14, 30))
                .subTotal(45000.0)
                .deliveryFee(1500L)
                .orderStatus(OrderStatus.PaymentReceived)
                .build();
        
        List<OrderItem> items = new ArrayList<>();
        
        // Item 1: Shoes
        ProductItemOrdered shoes = ProductItemOrdered.builder()
                .productId(101)
                .name("Nike Air Max")
                .pictureUrl("/images/nike-air-max.jpg")
                .build();
        OrderItem orderItem1 = OrderItem.builder()
                .itemOrdered(shoes)
                .price(15000L)
                .quantity(2)
                .order(order)
                .build();
        items.add(orderItem1);
        
        // Item 2: Shirt
        ProductItemOrdered shirt = ProductItemOrdered.builder()
                .productId(102)
                .name("Adidas Training Shirt")
                .pictureUrl("/images/adidas-shirt.jpg")
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .itemOrdered(shirt)
                .price(7500L)
                .quantity(2)
                .order(order)
                .build();
        items.add(orderItem2);
        
        order.setOrderItems(items);
        
        // Verify complete order
        assertEquals("basket-premium-001", order.getBasketId());
        assertEquals("Alice Johnson", order.getShippingAddress().getName());
        assertEquals("Seattle", order.getShippingAddress().getCity());
        assertEquals(45000.0, order.getSubTotal());
        assertEquals(1500L, order.getDeliveryFee());
        assertEquals(46500.0, order.getTotal());
        assertEquals(OrderStatus.PaymentReceived, order.getOrderStatus());
        assertEquals(2, order.getOrderItems().size());
        
        // Verify order items
        assertEquals("Nike Air Max", order.getOrderItems().get(0).getItemOrdered().getName());
        assertEquals(101, order.getOrderItems().get(0).getItemOrdered().getProductId());
        assertEquals(15000L, order.getOrderItems().get(0).getPrice());
        assertEquals(2, order.getOrderItems().get(0).getQuantity());
        
        assertEquals("Adidas Training Shirt", order.getOrderItems().get(1).getItemOrdered().getName());
        assertEquals(7500L, order.getOrderItems().get(1).getPrice());
    }

    @Test
    void testOrderItem_Equality() {
        ProductItemOrdered item = new ProductItemOrdered(1, "Item", "/pic");
        
        OrderItem orderItem1 = OrderItem.builder()
                .id(1)
                .itemOrdered(item)
                .price(1000L)
                .quantity(2)
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .id(1)
                .itemOrdered(item)
                .price(1000L)
                .quantity(2)
                .build();
        
        OrderItem orderItem3 = OrderItem.builder()
                .id(2)
                .itemOrdered(item)
                .price(1000L)
                .quantity(2)
                .build();
        
        assertEquals(orderItem1, orderItem2);
        assertNotEquals(orderItem1, orderItem3);
        assertEquals(orderItem1.hashCode(), orderItem2.hashCode());
    }

    @Test
    void testOrder_Equality() {
        Order order1 = Order.builder()
                .id(1)
                .basketId("basket")
                .subTotal(1000.0)
                .deliveryFee(100L)
                .build();
        
        Order order2 = Order.builder()
                .id(1)
                .basketId("basket")
                .subTotal(1000.0)
                .deliveryFee(100L)
                .build();
        
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testShippingAddress_WithNullFields() {
        ShippingAddress address = ShippingAddress.builder()
                .name("Name")
                .address1("Address")
                .city("City")
                .state("State")
                .zipcode("12345")
                .country("Country")
                .build();
        
        // address2 can be null
        assertNull(address.getAddress2());
        
        address.setAddress2(null);
        assertNull(address.getAddress2());
    }

    @Test
    void testOrderAggregate_AllConstructors() {
        // Test all-args constructors
        ShippingAddress addr = new ShippingAddress("N", "A1", "A2", "C", "S", "Z", "Co");
        ProductItemOrdered prod = new ProductItemOrdered(1, "P", "/pic");
        
        LocalDateTime date = LocalDateTime.now();
        Order order = new Order(1, "basket", addr, date, null, 1000.0, 100L, OrderStatus.Pending);
        
        assertEquals(1, order.getId());
        assertEquals("basket", order.getBasketId());
        
        OrderItem item = new OrderItem(1, prod, 100L, 1, order);
        assertEquals(1, item.getId());
        assertEquals(100L, item.getPrice());
    }
}
