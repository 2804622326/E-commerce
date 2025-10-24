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

    @Test
    void testOrderEqualsWithNullFields() {
        Order order1 = new Order(null, null, null, null, null, null, null, null);
        Order order2 = new Order(null, null, null, null, null, null, null, null);
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testOrderEqualsSameInstance() {
        Order order = Order.builder().id(1).basketId("basket").build();
        assertEquals(order, order);
        assertTrue(order.equals(order));
    }

    @Test
    void testOrderEqualsWithNull() {
        Order order = Order.builder().id(1).build();
        assertNotEquals(order, null);
        assertFalse(order.equals(null));
    }

    @Test
    void testOrderEqualsDifferentClass() {
        Order order = Order.builder().id(1).build();
        assertNotEquals(order, "String");
        assertNotEquals(order, Integer.valueOf(1));
        assertFalse(order.equals("String"));
    }

    @Test
    void testOrderEqualsWithDifferentFields() {
        Order order1 = Order.builder().id(1).basketId("basket1").subTotal(100.0).build();
        Order order2 = Order.builder().id(1).basketId("basket1").subTotal(100.0).build();
        Order order3 = Order.builder().id(2).basketId("basket1").subTotal(100.0).build();
        Order order4 = Order.builder().id(1).basketId("basket2").subTotal(100.0).build();
        
        assertEquals(order1, order2);
        assertNotEquals(order1, order3);
        assertNotEquals(order1, order4);
    }

    @Test
    void testOrderItemEqualsAndHashCode() {
        ProductItemOrdered prod1 = new ProductItemOrdered(1, "Prod", "/pic");
        ProductItemOrdered prod2 = new ProductItemOrdered(1, "Prod", "/pic");
        
        OrderItem item1 = OrderItem.builder().id(1).itemOrdered(prod1).price(100L).quantity(1).build();
        OrderItem item2 = OrderItem.builder().id(1).itemOrdered(prod2).price(100L).quantity(1).build();
        OrderItem item3 = OrderItem.builder().id(2).itemOrdered(prod1).price(100L).quantity(1).build();
        
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
        
        assertNotEquals(item1, null);
        assertNotEquals(item1, "String");
    }

    @Test
    void testProductItemOrderedEqualsAndHashCode() {
        ProductItemOrdered prod1 = new ProductItemOrdered(1, "Product", "/pic.jpg");
        ProductItemOrdered prod2 = new ProductItemOrdered(1, "Product", "/pic.jpg");
        ProductItemOrdered prod3 = new ProductItemOrdered(2, "Product", "/pic.jpg");
        ProductItemOrdered prod4 = new ProductItemOrdered(1, "Different", "/pic.jpg");
        
        assertEquals(prod1, prod2);
        assertEquals(prod1.hashCode(), prod2.hashCode());
        assertNotEquals(prod1, prod3);
        assertNotEquals(prod1, prod4);
        
        assertEquals(prod1, prod1);
        assertNotEquals(prod1, null);
        assertNotEquals(prod1, "String");
    }

    @Test
    void testShippingAddressEqualsAndHashCode() {
        ShippingAddress addr1 = new ShippingAddress("N", "A1", "A2", "C", "S", "Z", "Co");
        ShippingAddress addr2 = new ShippingAddress("N", "A1", "A2", "C", "S", "Z", "Co");
        ShippingAddress addr3 = new ShippingAddress("X", "A1", "A2", "C", "S", "Z", "Co");
        
        assertEquals(addr1, addr2);
        assertEquals(addr1.hashCode(), addr2.hashCode());
        assertNotEquals(addr1, addr3);
        
        assertNotEquals(addr1, null);
        assertNotEquals(addr1, "String");
        assertEquals(addr1, addr1);
    }

    @Test
    void testOrderToStringContainsFields() {
        Order order = Order.builder()
                .id(1)
                .basketId("test-basket")
                .subTotal(1000.0)
                .deliveryFee(100L)
                .orderStatus(OrderStatus.Pending)
                .build();
        
        String str = order.toString();
        assertNotNull(str);
        assertTrue(str.contains("test-basket") || str.contains("Order"));
    }

    @Test
    void testOrderItemToStringContainsFields() {
        ProductItemOrdered prod = new ProductItemOrdered(1, "Product", "/pic");
        OrderItem item = OrderItem.builder()
                .id(1)
                .itemOrdered(prod)
                .price(100L)
                .quantity(2)
                .build();
        
        String str = item.toString();
        assertNotNull(str);
        assertTrue(str.contains("OrderItem") || str.contains("100"));
    }

    @Test
    void testProductItemOrderedToStringContainsFields() {
        ProductItemOrdered prod = new ProductItemOrdered(1, "Test Product", "/test.jpg");
        
        String str = prod.toString();
        assertNotNull(str);
        assertTrue(str.contains("Test Product") || str.contains("ProductItemOrdered"));
    }

    @Test
    void testShippingAddressToStringContainsFields() {
        ShippingAddress addr = new ShippingAddress("John", "123 Main", null, "NYC", "NY", "10001", "USA");
        
        String str = addr.toString();
        assertNotNull(str);
        assertTrue(str.contains("John") || str.contains("ShippingAddress"));
    }

    @Test
    void testOrderHashCodeConsistency() {
        Order order = Order.builder().id(1).basketId("basket").build();
        int hash1 = order.hashCode();
        int hash2 = order.hashCode();
        int hash3 = order.hashCode();
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    void testOrderItemHashCodeConsistency() {
        OrderItem item = OrderItem.builder().id(1).price(100L).build();
        int hash1 = item.hashCode();
        int hash2 = item.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testProductItemOrderedHashCodeConsistency() {
        ProductItemOrdered prod = new ProductItemOrdered(1, "Prod", "/pic");
        int hash1 = prod.hashCode();
        int hash2 = prod.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testShippingAddressHashCodeConsistency() {
        ShippingAddress addr = new ShippingAddress("N", "A", null, "C", "S", "Z", "Co");
        int hash1 = addr.hashCode();
        int hash2 = addr.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testOrderWithEmptyStrings() {
        Order order = Order.builder()
                .id(1)
                .basketId("")
                .subTotal(0.0)
                .deliveryFee(0L)
                .build();
        
        assertEquals("", order.getBasketId());
        assertEquals(0.0, order.getSubTotal());
        assertEquals(0L, order.getDeliveryFee());
    }

    @Test
    void testProductItemOrderedWithEmptyStrings() {
        ProductItemOrdered prod = new ProductItemOrdered(1, "", "");
        assertEquals(1, prod.getProductId());
        assertEquals("", prod.getName());
        assertEquals("", prod.getPictureUrl());
    }

    @Test
    void testShippingAddressWithEmptyStrings() {
        ShippingAddress addr = new ShippingAddress("", "", "", "", "", "", "");
        assertEquals("", addr.getName());
        assertEquals("", addr.getAddress1());
        assertEquals("", addr.getCity());
    }

    @Test
    void testOrderItemWithNullOrder() {
        ProductItemOrdered prod = new ProductItemOrdered(1, "Prod", "/pic");
        OrderItem item = OrderItem.builder()
                .id(1)
                .itemOrdered(prod)
                .price(100L)
                .quantity(1)
                .order(null)
                .build();
        
        assertNull(item.getOrder());
        
        item.setOrder(null);
        assertNull(item.getOrder());
    }

    @Test
    void testOrderWithZeroValues() {
        Order order = Order.builder()
                .id(0)
                .basketId("basket")
                .subTotal(0.0)
                .deliveryFee(0L)
                .build();
        
        assertEquals(0, order.getId());
        assertEquals(0.0, order.getSubTotal());
        assertEquals(0L, order.getDeliveryFee());
        assertEquals(0.0, order.getTotal());
    }

    @Test
    void testOrderItemWithZeroValues() {
        OrderItem item = OrderItem.builder()
                .id(0)
                .price(0L)
                .quantity(0)
                .build();
        
        assertEquals(0, item.getId());
        assertEquals(0L, item.getPrice());
        assertEquals(0, item.getQuantity());
    }

    @Test
    void testProductItemOrderedWithZeroId() {
        ProductItemOrdered prod = new ProductItemOrdered(0, "Product", "/pic");
        assertEquals(0, prod.getProductId());
        assertEquals("Product", prod.getName());
    }
    
    @Test
    void testOrderItem_ComprehensiveEqualsAndHashCode() {
        ProductItemOrdered prod1 = new ProductItemOrdered(1, "Product1", "/pic1.jpg");
        ProductItemOrdered prod2 = new ProductItemOrdered(1, "Product1", "/pic1.jpg");
        ProductItemOrdered prod3 = new ProductItemOrdered(2, "Product2", "/pic2.jpg");
        
        OrderItem oi1 = OrderItem.builder().id(1).itemOrdered(prod1).price(100L).quantity(2).build();
        OrderItem oi2 = OrderItem.builder().id(1).itemOrdered(prod2).price(100L).quantity(2).build();
        OrderItem oi3 = OrderItem.builder().id(2).itemOrdered(prod1).price(100L).quantity(2).build();
        OrderItem oi4 = OrderItem.builder().id(1).itemOrdered(prod3).price(100L).quantity(2).build();
        OrderItem oi5 = OrderItem.builder().id(1).itemOrdered(prod1).price(200L).quantity(2).build();
        OrderItem oi6 = OrderItem.builder().id(1).itemOrdered(prod1).price(100L).quantity(3).build();
        OrderItem oi7 = OrderItem.builder().itemOrdered(prod1).price(100L).quantity(2).build();
        OrderItem oi8 = OrderItem.builder().id(1).price(100L).quantity(2).build();
        OrderItem oi9 = OrderItem.builder().id(1).itemOrdered(prod1).quantity(2).build();
        OrderItem oi10 = OrderItem.builder().id(1).itemOrdered(prod1).price(100L).build();
        
        // Equals
        assertEquals(oi1, oi2);
        assertNotEquals(oi1, oi3);
        assertNotEquals(oi1, oi4);
        assertNotEquals(oi1, oi5);
        assertNotEquals(oi1, oi6);
        assertNotEquals(oi1, oi7);
        assertNotEquals(oi1, oi8);
        assertNotEquals(oi1, oi9);
        assertNotEquals(oi1, oi10);
        assertNotEquals(oi1, null);
        assertEquals(oi1, oi1);
        
        // HashCode
        assertEquals(oi1.hashCode(), oi2.hashCode());
        
        // ToString
        assertNotNull(oi1.toString());
        assertTrue(oi1.toString().contains("Product1"));
        
        OrderItem oin = new OrderItem();
        assertNotNull(oin.toString());
    }
    
    @Test
    void testProductItemOrdered_ComprehensiveEqualsAndHashCode() {
        ProductItemOrdered p1 = new ProductItemOrdered(1, "Product1", "/pic1.jpg");
        ProductItemOrdered p2 = new ProductItemOrdered(1, "Product1", "/pic1.jpg");
        ProductItemOrdered p3 = new ProductItemOrdered(2, "Product1", "/pic1.jpg");
        ProductItemOrdered p4 = new ProductItemOrdered(1, "Product2", "/pic1.jpg");
        ProductItemOrdered p5 = new ProductItemOrdered(1, "Product1", "/pic2.jpg");
        ProductItemOrdered p6 = new ProductItemOrdered(null, "Product1", "/pic1.jpg");
        ProductItemOrdered p7 = new ProductItemOrdered(1, null, "/pic1.jpg");
        ProductItemOrdered p8 = new ProductItemOrdered(1, "Product1", null);
        
        // Equals
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p1, p4);
        assertNotEquals(p1, p5);
        assertNotEquals(p1, p6);
        assertNotEquals(p1, p7);
        assertNotEquals(p1, p8);
        assertNotEquals(p1, null);
        assertEquals(p1, p1);
        
        // HashCode
        assertEquals(p1.hashCode(), p2.hashCode());
        
        // ToString
        assertNotNull(p1.toString());
        assertTrue(p1.toString().contains("Product1"));
        
        ProductItemOrdered pn = new ProductItemOrdered(null, null, null);
        assertNotNull(pn.toString());
    }
    
    @Test
    void testShippingAddress_ComprehensiveEqualsAndHashCode() {
        ShippingAddress a1 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress a2 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress a3 = new ShippingAddress("Jane", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress a4 = new ShippingAddress("John", "456 Ave", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress a5 = new ShippingAddress("John", "123 St", "Apt 2", "City", "State", "12345", "USA");
        ShippingAddress a6 = new ShippingAddress("John", "123 St", "Apt 1", "Town", "State", "12345", "USA");
        ShippingAddress a7 = new ShippingAddress("John", "123 St", "Apt 1", "City", "Province", "12345", "USA");
        ShippingAddress a8 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "54321", "USA");
        ShippingAddress a9 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "Canada");
        ShippingAddress a10 = new ShippingAddress(null, "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress a11 = new ShippingAddress("John", null, "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress a12 = new ShippingAddress("John", "123 St", null, "City", "State", "12345", "USA");
        ShippingAddress a13 = new ShippingAddress("John", "123 St", "Apt 1", null, "State", "12345", "USA");
        ShippingAddress a14 = new ShippingAddress("John", "123 St", "Apt 1", "City", null, "12345", "USA");
        ShippingAddress a15 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", null, "USA");
        ShippingAddress a16 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", null);
        
        // Equals
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, a4);
        assertNotEquals(a1, a5);
        assertNotEquals(a1, a6);
        assertNotEquals(a1, a7);
        assertNotEquals(a1, a8);
        assertNotEquals(a1, a9);
        assertNotEquals(a1, a10);
        assertNotEquals(a1, a11);
        assertNotEquals(a1, a12);
        assertNotEquals(a1, a13);
        assertNotEquals(a1, a14);
        assertNotEquals(a1, a15);
        assertNotEquals(a1, a16);
        assertNotEquals(a1, null);
        assertEquals(a1, a1);
        
        // HashCode
        assertEquals(a1.hashCode(), a2.hashCode());
        
        // ToString
        assertNotNull(a1.toString());
        assertTrue(a1.toString().contains("John"));
        
        ShippingAddress an = new ShippingAddress(null, null, null, null, null, null, null);
        assertNotNull(an.toString());
    }
    
    @Test
    void testOrder_ComprehensiveEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ShippingAddress addr1 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress addr2 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress addr3 = new ShippingAddress("Jane", "456 Ave", "Unit 2", "Town", "Province", "54321", "Canada");
        
        List<OrderItem> items1 = new ArrayList<>();
        items1.add(OrderItem.builder().id(1).itemOrdered(new ProductItemOrdered(1, "Prod1", "/pic1.jpg")).price(100L).quantity(2).build());
        
        List<OrderItem> items2 = new ArrayList<>();
        items2.add(OrderItem.builder().id(1).itemOrdered(new ProductItemOrdered(1, "Prod1", "/pic1.jpg")).price(100L).quantity(2).build());
        
        List<OrderItem> items3 = new ArrayList<>();
        items3.add(OrderItem.builder().id(2).itemOrdered(new ProductItemOrdered(2, "Prod2", "/pic2.jpg")).price(200L).quantity(1).build());
        
        Order o1 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o2 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr2).orderStatus(OrderStatus.Pending).orderItems(items2).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o3 = Order.builder().id(2).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o4 = Order.builder().id(1).basketId("basket-2").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o5 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr3).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o6 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.PaymentReceived).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o7 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items3).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o8 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(200.0).deliveryFee(10L).orderDate(now).build();
        Order o9 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(20L).orderDate(now).build();
        Order o10 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now.plusDays(1)).build();
        Order o11 = Order.builder().basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o12 = Order.builder().id(1).shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o13 = Order.builder().id(1).basketId("basket-1").orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o14 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderItems(items1).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o15 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).subTotal(100.0).deliveryFee(10L).orderDate(now).build();
        Order o16 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).deliveryFee(10L).orderDate(now).build();
        Order o17 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).orderDate(now).build();
        Order o18 = Order.builder().id(1).basketId("basket-1").shippingAddress(addr1).orderStatus(OrderStatus.Pending).orderItems(items1).subTotal(100.0).deliveryFee(10L).build();
        
        // Equals
        assertEquals(o1, o2);
        assertNotEquals(o1, o3);
        assertNotEquals(o1, o4);
        assertNotEquals(o1, o5);
        assertNotEquals(o1, o6);
        assertNotEquals(o1, o7);
        assertNotEquals(o1, o8);
        assertNotEquals(o1, o9);
        assertNotEquals(o1, o10);
        assertNotEquals(o1, o11);
        assertNotEquals(o1, o12);
        assertNotEquals(o1, o13);
        assertNotEquals(o1, o14);
        assertNotEquals(o1, o15);
        assertNotEquals(o1, o16);
        assertNotEquals(o1, o17);
        assertNotEquals(o1, o18);
        assertNotEquals(o1, null);
        assertEquals(o1, o1);
        
        // HashCode
        assertEquals(o1.hashCode(), o2.hashCode());
        
        // ToString
        assertNotNull(o1.toString());
        assertTrue(o1.toString().contains("basket-1"));
        
        Order on = new Order();
        assertNotNull(on.toString());
    }
}
