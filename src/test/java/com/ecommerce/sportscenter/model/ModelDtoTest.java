package com.ecommerce.sportscenter.model;

import com.ecommerce.sportscenter.entity.OrderAggregate.OrderStatus;
import com.ecommerce.sportscenter.entity.OrderAggregate.ShippingAddress;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Model DTOs
 * Tests all Lombok generated methods (getters, setters, builders, equals, hashCode, toString)
 */
class ModelDtoTest {

    @Test
    void testJwtRequest_AllMethods() {
        // Test builder
        JwtRequest request = JwtRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
        
        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
        
        // Test setters
        request.setUsername("newuser");
        request.setPassword("newpass");
        assertEquals("newuser", request.getUsername());
        assertEquals("newpass", request.getPassword());
        
        // Test no-args constructor
        JwtRequest emptyRequest = new JwtRequest();
        assertNull(emptyRequest.getUsername());
        assertNull(emptyRequest.getPassword());
        
        // Test all-args constructor
        JwtRequest fullRequest = new JwtRequest("user", "pass");
        assertEquals("user", fullRequest.getUsername());
        assertEquals("pass", fullRequest.getPassword());
        
        // Test equals and hashCode
        JwtRequest request1 = new JwtRequest("user1", "pass1");
        JwtRequest request2 = new JwtRequest("user1", "pass1");
        JwtRequest request3 = new JwtRequest("user2", "pass2");
        
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        
        // Test toString
        assertNotNull(request1.toString());
        assertTrue(request1.toString().contains("user1"));
    }

    @Test
    void testJwtResponse_AllMethods() {
        JwtResponse response = JwtResponse.builder()
                .username("testuser")
                .token("jwt.token.here")
                .build();
        
        assertEquals("testuser", response.getUsername());
        assertEquals("jwt.token.here", response.getToken());
        
        response.setUsername("newuser");
        response.setToken("new.token");
        assertEquals("newuser", response.getUsername());
        assertEquals("new.token", response.getToken());
        
        JwtResponse emptyResponse = new JwtResponse();
        assertNull(emptyResponse.getUsername());
        
        JwtResponse fullResponse = new JwtResponse("user", "token");
        assertEquals("user", fullResponse.getUsername());
        assertEquals("token", fullResponse.getToken());
        
        assertNotNull(response.toString());
        assertEquals(response, response);
        assertNotEquals(response, fullResponse);
    }

    @Test
    void testBrandResponse_AllMethods() {
        BrandResponse brand = BrandResponse.builder()
                .id(1)
                .name("Nike")
                .build();
        
        assertEquals(1, brand.getId());
        assertEquals("Nike", brand.getName());
        
        brand.setId(2);
        brand.setName("Adidas");
        assertEquals(2, brand.getId());
        assertEquals("Adidas", brand.getName());
        
        BrandResponse emptyBrand = new BrandResponse();
        assertNull(emptyBrand.getId());
        assertNull(emptyBrand.getName());
        
        BrandResponse fullBrand = new BrandResponse(3, "Puma");
        assertEquals(3, fullBrand.getId());
        assertEquals("Puma", fullBrand.getName());
        
        assertNotNull(brand.toString());
        assertTrue(brand.toString().contains("Adidas"));
        assertEquals(brand.hashCode(), brand.hashCode());
    }

    @Test
    void testTypeResponse_AllMethods() {
        TypeResponse type = TypeResponse.builder()
                .id(1)
                .name("Shoes")
                .build();
        
        assertEquals(1, type.getId());
        assertEquals("Shoes", type.getName());
        
        type.setId(2);
        type.setName("Clothing");
        assertEquals(2, type.getId());
        assertEquals("Clothing", type.getName());
        
        TypeResponse emptyType = new TypeResponse();
        assertNull(emptyType.getId());
        
        TypeResponse fullType = new TypeResponse(3, "Equipment");
        assertEquals(3, fullType.getId());
        
        assertNotNull(type.toString());
    }

    @Test
    void testProductResponse_AllMethods() {
        ProductResponse product = ProductResponse.builder()
                .id(1)
                .name("Running Shoes")
                .description("Comfortable running shoes")
                .price(8999L)
                .pictureUrl("/images/shoes.jpg")
                .productBrand("Nike")
                .productType("Shoes")
                .build();
        
        assertEquals(1, product.getId());
        assertEquals("Running Shoes", product.getName());
        assertEquals("Comfortable running shoes", product.getDescription());
        assertEquals(8999L, product.getPrice());
        assertEquals("/images/shoes.jpg", product.getPictureUrl());
        assertEquals("Nike", product.getProductBrand());
        assertEquals("Shoes", product.getProductType());
        
        product.setId(2);
        product.setName("New Name");
        product.setDescription("New Description");
        product.setPrice(5000L);
        product.setPictureUrl("/new.jpg");
        product.setProductBrand("Adidas");
        product.setProductType("Clothing");
        
        assertEquals(2, product.getId());
        assertEquals("New Name", product.getName());
        assertEquals("New Description", product.getDescription());
        assertEquals(5000L, product.getPrice());
        assertEquals("/new.jpg", product.getPictureUrl());
        assertEquals("Adidas", product.getProductBrand());
        assertEquals("Clothing", product.getProductType());
        
        ProductResponse emptyProduct = new ProductResponse();
        assertNull(emptyProduct.getId());
        
        assertNotNull(product.toString());
        assertTrue(product.toString().contains("New Name"));
    }

    @Test
    void testBasketItemResponse_AllMethods() {
        BasketItemResponse item = BasketItemResponse.builder()
                .id(1)
                .name("Product")
                .description("Description")
                .price(1000L)
                .pictureUrl("/pic.jpg")
                .productBrand("Brand")
                .productType("Type")
                .quantity(2)
                .build();
        
        assertEquals(1, item.getId());
        assertEquals("Product", item.getName());
        assertEquals("Description", item.getDescription());
        assertEquals(1000L, item.getPrice());
        assertEquals("/pic.jpg", item.getPictureUrl());
        assertEquals("Brand", item.getProductBrand());
        assertEquals("Type", item.getProductType());
        assertEquals(2, item.getQuantity());
        
        item.setId(2);
        item.setName("New Product");
        item.setDescription("New Desc");
        item.setPrice(2000L);
        item.setPictureUrl("/new.jpg");
        item.setProductBrand("New Brand");
        item.setProductType("New Type");
        item.setQuantity(5);
        
        assertEquals(2, item.getId());
        assertEquals("New Product", item.getName());
        assertEquals("New Desc", item.getDescription());
        assertEquals(2000L, item.getPrice());
        assertEquals("/new.jpg", item.getPictureUrl());
        assertEquals("New Brand", item.getProductBrand());
        assertEquals("New Type", item.getProductType());
        assertEquals(5, item.getQuantity());
        
        BasketItemResponse emptyItem = new BasketItemResponse();
        assertNull(emptyItem.getId());
        
        assertNotNull(item.toString());
        assertEquals(item, item);
    }

    @Test
    void testBasketResponse_AllMethods() {
        List<BasketItemResponse> items = new ArrayList<>();
        items.add(BasketItemResponse.builder().id(1).name("Item1").quantity(2).build());
        
        BasketResponse basket = BasketResponse.builder()
                .id("basket-123")
                .items(items)
                .build();
        
        assertEquals("basket-123", basket.getId());
        assertEquals(1, basket.getItems().size());
        assertEquals("Item1", basket.getItems().get(0).getName());
        
        basket.setId("basket-456");
        List<BasketItemResponse> newItems = new ArrayList<>();
        basket.setItems(newItems);
        
        assertEquals("basket-456", basket.getId());
        assertEquals(0, basket.getItems().size());
        
        BasketResponse emptyBasket = new BasketResponse();
        assertNull(emptyBasket.getId());
        
        BasketResponse fullBasket = new BasketResponse("id", items);
        assertEquals("id", fullBasket.getId());
        
        assertNotNull(basket.toString());
    }

    @Test
    void testOrderDto_AllMethods() {
        ShippingAddress address = ShippingAddress.builder()
                .name("John Doe")
                .address1("123 Main St")
                .city("New York")
                .state("NY")
                .zipcode("10001")
                .country("USA")
                .build();
        
        LocalDateTime orderDate = LocalDateTime.of(2024, 1, 15, 10, 30);
        
        OrderDto orderDto = OrderDto.builder()
                .basketId("basket-123")
                .shippingAddress(address)
                .subTotal(10000L)
                .deliveryFee(500L)
                .orderDate(orderDate)
                .build();
        
        assertEquals("basket-123", orderDto.getBasketId());
        assertEquals(address, orderDto.getShippingAddress());
        assertEquals(10000L, orderDto.getSubTotal());
        assertEquals(500L, orderDto.getDeliveryFee());
        assertEquals(orderDate, orderDto.getOrderDate());
        
        orderDto.setBasketId("new-basket");
        orderDto.setSubTotal(20000L);
        orderDto.setDeliveryFee(1000L);
        
        assertEquals("new-basket", orderDto.getBasketId());
        assertEquals(20000L, orderDto.getSubTotal());
        assertEquals(1000L, orderDto.getDeliveryFee());
        
        OrderDto emptyDto = new OrderDto();
        assertNull(emptyDto.getBasketId());
        
        assertNotNull(orderDto.toString());
    }

    @Test
    void testOrderResponse_AllMethods() {
        ShippingAddress address = ShippingAddress.builder()
                .name("Jane Doe")
                .address1("456 Oak St")
                .city("Boston")
                .state("MA")
                .zipcode("02101")
                .country("USA")
                .build();
        
        LocalDateTime orderDate = LocalDateTime.of(2024, 2, 20, 14, 0);
        
        OrderResponse response = OrderResponse.builder()
                .id(1)
                .basketId("basket-456")
                .shippingAddress(address)
                .subTotal(15000L)
                .deliveryFee(750L)
                .total(15750.0)
                .orderDate(orderDate)
                .orderStatus(OrderStatus.Pending)
                .build();
        
        assertEquals(1, response.getId());
        assertEquals("basket-456", response.getBasketId());
        assertEquals(address, response.getShippingAddress());
        assertEquals(15000L, response.getSubTotal());
        assertEquals(750L, response.getDeliveryFee());
        assertEquals(15750.0, response.getTotal());
        assertEquals(orderDate, response.getOrderDate());
        assertEquals(OrderStatus.Pending, response.getOrderStatus());
        
        response.setId(2);
        response.setBasketId("new-basket");
        response.setSubTotal(20000L);
        response.setDeliveryFee(1000L);
        response.setTotal(21000.0);
        response.setOrderStatus(OrderStatus.PaymentReceived);
        
        assertEquals(2, response.getId());
        assertEquals("new-basket", response.getBasketId());
        assertEquals(20000L, response.getSubTotal());
        assertEquals(1000L, response.getDeliveryFee());
        assertEquals(21000.0, response.getTotal());
        assertEquals(OrderStatus.PaymentReceived, response.getOrderStatus());
        
        OrderResponse emptyResponse = new OrderResponse();
        assertNull(emptyResponse.getId());
        
        assertNotNull(response.toString());
        assertTrue(response.toString().contains("new-basket"));
    }

    @Test
    void testCustomErrorResponse_AllMethods() {
        CustomErrorResponse error = new CustomErrorResponse(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Bad Request",
                "Error message"
        );
        
        assertEquals("Error message", error.getMessage());
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals("Bad Request", error.getError());
        
        error.setMessage("New error");
        error.setStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        error.setError("Internal Server Error");
        
        assertEquals("New error", error.getMessage());
        assertEquals(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus());
        assertEquals("Internal Server Error", error.getError());
        
        CustomErrorResponse emptyError = new CustomErrorResponse();
        assertNull(emptyError.getMessage());
        
        assertNotNull(error.toString());
    }

    @Test
    void testShippingAddress_AllMethods() {
        ShippingAddress address = ShippingAddress.builder()
                .name("Test User")
                .address1("123 Test St")
                .address2("Apt 4B")
                .city("Test City")
                .state("TC")
                .zipcode("12345")
                .country("Test Country")
                .build();
        
        assertEquals("Test User", address.getName());
        assertEquals("123 Test St", address.getAddress1());
        assertEquals("Apt 4B", address.getAddress2());
        assertEquals("Test City", address.getCity());
        assertEquals("TC", address.getState());
        assertEquals("12345", address.getZipcode());
        assertEquals("Test Country", address.getCountry());
        
        address.setName("New Name");
        address.setAddress1("New Address1");
        address.setAddress2("New Address2");
        address.setCity("New City");
        address.setState("NS");
        address.setZipcode("54321");
        address.setCountry("New Country");
        
        assertEquals("New Name", address.getName());
        assertEquals("New Address1", address.getAddress1());
        assertEquals("New Address2", address.getAddress2());
        assertEquals("New City", address.getCity());
        assertEquals("NS", address.getState());
        assertEquals("54321", address.getZipcode());
        assertEquals("New Country", address.getCountry());
        
        ShippingAddress emptyAddress = new ShippingAddress();
        assertNull(emptyAddress.getName());
        
        ShippingAddress fullAddress = new ShippingAddress(
                "Name", "Addr1", "Addr2", "City", "State", "Zip", "Country"
        );
        assertEquals("Name", fullAddress.getName());
        
        assertNotNull(address.toString());
        assertEquals(address, address);
        assertNotEquals(address, emptyAddress);
    }

    @Test
    void testModelEquality() {
        // Test JwtRequest equality
        JwtRequest req1 = new JwtRequest("user", "pass");
        JwtRequest req2 = new JwtRequest("user", "pass");
        JwtRequest req3 = new JwtRequest("diff", "pass");
        
        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertNotEquals(req1, null);
        assertNotEquals(req1, "string");
        
        // Test ProductResponse equality
        ProductResponse prod1 = ProductResponse.builder().id(1).name("Prod").build();
        ProductResponse prod2 = ProductResponse.builder().id(1).name("Prod").build();
        ProductResponse prod3 = ProductResponse.builder().id(2).name("Prod").build();
        
        assertEquals(prod1, prod2);
        assertNotEquals(prod1, prod3);
    }

    @Test
    void testModelHashCode() {
        JwtRequest req1 = new JwtRequest("user", "pass");
        JwtRequest req2 = new JwtRequest("user", "pass");
        
        assertEquals(req1.hashCode(), req2.hashCode());
        
        BrandResponse brand1 = new BrandResponse(1, "Nike");
        BrandResponse brand2 = new BrandResponse(1, "Nike");
        
        assertEquals(brand1.hashCode(), brand2.hashCode());
    }
}
