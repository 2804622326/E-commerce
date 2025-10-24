package com.ecommerce.sportscenter.model;

import com.ecommerce.sportscenter.entity.OrderAggregate.OrderStatus;
import com.ecommerce.sportscenter.entity.OrderAggregate.ShippingAddress;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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

    @Test
    void testAllModelsWithNullFields() {
        // Test JwtRequest with nulls
        JwtRequest jwtReq = new JwtRequest(null, null);
        assertNull(jwtReq.getUsername());
        assertNull(jwtReq.getPassword());
        assertNotNull(jwtReq.toString());
        
        // Test JwtResponse with nulls
        JwtResponse jwtResp = new JwtResponse(null, null);
        assertNull(jwtResp.getUsername());
        assertNull(jwtResp.getToken());
        
        // Test BrandResponse with nulls
        BrandResponse brand = new BrandResponse(null, null);
        assertNull(brand.getId());
        assertNull(brand.getName());
        
        // Test TypeResponse with nulls
        TypeResponse type = new TypeResponse(null, null);
        assertNull(type.getId());
        assertNull(type.getName());
        
        // Test ProductResponse with nulls
        ProductResponse product = ProductResponse.builder().build();
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getPrice());
        
        // Test BasketItemResponse with nulls
        BasketItemResponse item = BasketItemResponse.builder().build();
        assertNull(item.getId());
        assertNull(item.getQuantity());
        
        // Test BasketResponse with nulls
        BasketResponse basket = BasketResponse.builder().build();
        assertNull(basket.getId());
        assertNull(basket.getItems());
        
        // Test OrderDto with nulls
        OrderDto orderDto = OrderDto.builder().build();
        assertNull(orderDto.getBasketId());
        assertNull(orderDto.getShippingAddress());
        
        // Test OrderResponse with nulls
        OrderResponse orderResp = OrderResponse.builder().build();
        assertNull(orderResp.getId());
        assertNull(orderResp.getBasketId());
    }

    @Test
    void testEqualsSameObject() {
        JwtRequest req = new JwtRequest("user", "pass");
        assertEquals(req, req);
        
        BrandResponse brand = new BrandResponse(1, "Nike");
        assertEquals(brand, brand);
        
        ProductResponse product = ProductResponse.builder().id(1).build();
        assertEquals(product, product);
    }

    @Test
    void testEqualsWithNull() {
        JwtRequest req = new JwtRequest("user", "pass");
        assertNotEquals(req, null);
        
        BrandResponse brand = new BrandResponse(1, "Nike");
        assertNotEquals(brand, null);
        
        ProductResponse product = ProductResponse.builder().id(1).build();
        assertNotEquals(product, null);
    }

    @Test
    void testEqualsDifferentClass() {
        JwtRequest req = new JwtRequest("user", "pass");
        assertNotEquals(req, "String");
        assertNotEquals(req, Integer.valueOf(1));
        
        BrandResponse brand = new BrandResponse(1, "Nike");
        assertNotEquals(brand, "String");
        
        ProductResponse product = ProductResponse.builder().id(1).build();
        assertNotEquals(product, new Object());
    }

    @Test
    void testHashCodeConsistency() {
        JwtRequest req = new JwtRequest("user", "pass");
        int hash1 = req.hashCode();
        int hash2 = req.hashCode();
        assertEquals(hash1, hash2);
        
        BrandResponse brand = new BrandResponse(1, "Nike");
        hash1 = brand.hashCode();
        hash2 = brand.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testToStringContainsFieldNames() {
        JwtRequest req = new JwtRequest("testuser", "testpass");
        String str = req.toString();
        assertTrue(str.contains("testuser") || str.contains("JwtRequest"));
        
        BrandResponse brand = new BrandResponse(1, "Nike");
        str = brand.toString();
        assertTrue(str.contains("Nike") || str.contains("BrandResponse"));
        
        ProductResponse product = ProductResponse.builder()
                .id(1)
                .name("Product")
                .price(1000L)
                .build();
        str = product.toString();
        assertTrue(str.contains("Product") || str.contains("1000"));
    }

    @Test
    void testBuilderWithPartialFields() {
        ProductResponse product = ProductResponse.builder()
                .id(1)
                .name("Partial")
                .build();
        
        assertEquals(1, product.getId());
        assertEquals("Partial", product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getPictureUrl());
        
        BasketItemResponse item = BasketItemResponse.builder()
                .id(1)
                .quantity(5)
                .build();
        
        assertEquals(1, item.getId());
        assertEquals(5, item.getQuantity());
        assertNull(item.getName());
        assertNull(item.getPrice());
    }

    @Test
    void testShippingAddressEdgeCases() {
        ShippingAddress addr1 = new ShippingAddress();
        ShippingAddress addr2 = new ShippingAddress();
        
        assertEquals(addr1, addr2);
        assertEquals(addr1.hashCode(), addr2.hashCode());
        
        addr1.setName("");
        addr1.setAddress1("");
        addr1.setCity("");
        addr1.setState("");
        addr1.setZipcode("");
        addr1.setCountry("");
        
        assertEquals("", addr1.getName());
        assertEquals("", addr1.getAddress1());
        
        ShippingAddress addr3 = new ShippingAddress("", "", "", "", "", "", "");
        assertEquals("", addr3.getName());
    }

    @Test
    void testCustomErrorResponseWithDifferentMessages() {
        CustomErrorResponse error1 = new CustomErrorResponse(HttpStatus.NOT_FOUND, "Error", "Message 1");
        CustomErrorResponse error2 = new CustomErrorResponse(HttpStatus.NOT_FOUND, "Error", "Message 2");
        
        assertNotEquals(error1, error2);
        assertEquals(error1.getStatus(), error2.getStatus());
        assertEquals(error1.getError(), error2.getError());
        assertNotEquals(error1.getMessage(), error2.getMessage());
    }

    @Test
    void testOrderDtoWithAllNulls() {
        OrderDto dto = new OrderDto(null, null, null, null, null);
        assertNull(dto.getBasketId());
        assertNull(dto.getShippingAddress());
        assertNull(dto.getSubTotal());
        assertNull(dto.getDeliveryFee());
        assertNull(dto.getOrderDate());
    }

    @Test
    void testOrderResponseAllFields() {
        ShippingAddress addr = ShippingAddress.builder()
                .name("Test")
                .address1("Addr1")
                .build();
        
        LocalDateTime date = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        OrderResponse resp = new OrderResponse(
                1, "basket", addr, 100L, 10L, 110.0, date, OrderStatus.Pending
        );
        
        assertEquals(1, resp.getId());
        assertEquals("basket", resp.getBasketId());
        assertEquals(100L, resp.getSubTotal());
        assertEquals(110.0, resp.getTotal());
        assertEquals(OrderStatus.Pending, resp.getOrderStatus());
    }

    @Test
    void testBasketResponseWithEmptyList() {
        BasketResponse basket = BasketResponse.builder()
                .id("empty-basket")
                .items(new ArrayList<>())
                .build();
        
        assertEquals("empty-basket", basket.getId());
        assertNotNull(basket.getItems());
        assertTrue(basket.getItems().isEmpty());
    }

    @Test
    void testBasketResponseWithNullItems() {
        BasketResponse basket = new BasketResponse("id", null);
        assertEquals("id", basket.getId());
        assertNull(basket.getItems());
        
        basket.setItems(new ArrayList<>());
        assertNotNull(basket.getItems());
    }

    // ===== Additional comprehensive branch coverage tests =====
    
    @Test
    void testOrderResponse_EqualsWithAllFieldCombinations() {
        LocalDateTime now = LocalDateTime.now();
        ShippingAddress addr = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        
        // Test all field combinations for equals
        OrderResponse r1 = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r2 = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r3 = new OrderResponse(2, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r4 = new OrderResponse(1, "b2", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r5 = new OrderResponse(1, "b1", null, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r6 = new OrderResponse(1, "b1", addr, 200L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r7 = new OrderResponse(1, "b1", addr, 100L, 20L, 110.0, now, OrderStatus.Pending);
        OrderResponse r8 = new OrderResponse(1, "b1", addr, 100L, 10L, 120.0, now, OrderStatus.Pending);
        OrderResponse r9 = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, null, OrderStatus.Pending);
        OrderResponse r10 = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.PaymentReceived);
        
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);  // different id
        assertNotEquals(r1, r4);  // different basketId
        assertNotEquals(r1, r5);  // different shippingAddress
        assertNotEquals(r1, r6);  // different subTotal
        assertNotEquals(r1, r7);  // different deliveryFee
        assertNotEquals(r1, r8);  // different total
        assertNotEquals(r1, r9);  // different orderDate
        assertNotEquals(r1, r10); // different orderStatus
        
        assertNotEquals(r1, null);
        assertNotEquals(r1, "not an OrderResponse");
        assertEquals(r1, r1); // same instance
    }
    
    @Test
    void testBasketItemResponse_EqualsWithAllFieldCombinations() {
        BasketItemResponse i1 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Brand", "Type", 5);
        BasketItemResponse i2 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Brand", "Type", 5);
        BasketItemResponse i3 = new BasketItemResponse(2, "Item", "Desc", 100L, "url", "Brand", "Type", 5);
        BasketItemResponse i4 = new BasketItemResponse(1, "Different", "Desc", 100L, "url", "Brand", "Type", 5);
        BasketItemResponse i5 = new BasketItemResponse(1, "Item", "Diff", 100L, "url", "Brand", "Type", 5);
        BasketItemResponse i6 = new BasketItemResponse(1, "Item", "Desc", 200L, "url", "Brand", "Type", 5);
        BasketItemResponse i7 = new BasketItemResponse(1, "Item", "Desc", 100L, "diff", "Brand", "Type", 5);
        BasketItemResponse i8 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Diff", "Type", 5);
        BasketItemResponse i9 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Brand", "Diff", 5);
        BasketItemResponse i10 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Brand", "Type", 10);
        
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);  // different id
        assertNotEquals(i1, i4);  // different name
        assertNotEquals(i1, i5);  // different description
        assertNotEquals(i1, i6);  // different price
        assertNotEquals(i1, i7);  // different pictureUrl
        assertNotEquals(i1, i8);  // different productBrand
        assertNotEquals(i1, i9);  // different productType
        assertNotEquals(i1, i10); // different quantity
        
        assertNotEquals(i1, null);
        assertNotEquals(i1, new Object());
    }
    
    @Test
    void testProductResponse_EqualsWithAllFieldCombinations() {
        ProductResponse p1 = new ProductResponse(1, "Product", "Description", 100L, "url", "Brand", "Type");
        ProductResponse p2 = new ProductResponse(1, "Product", "Description", 100L, "url", "Brand", "Type");
        ProductResponse p3 = new ProductResponse(2, "Product", "Description", 100L, "url", "Brand", "Type");
        ProductResponse p4 = new ProductResponse(1, "Diff", "Description", 100L, "url", "Brand", "Type");
        ProductResponse p5 = new ProductResponse(1, "Product", "Diff", 100L, "url", "Brand", "Type");
        ProductResponse p6 = new ProductResponse(1, "Product", "Description", 200L, "url", "Brand", "Type");
        ProductResponse p7 = new ProductResponse(1, "Product", "Description", 100L, "diff", "Brand", "Type");
        ProductResponse p8 = new ProductResponse(1, "Product", "Description", 100L, "url", "Diff", "Type");
        ProductResponse p9 = new ProductResponse(1, "Product", "Description", 100L, "url", "Brand", "Diff");
        
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);  // different id
        assertNotEquals(p1, p4);  // different name
        assertNotEquals(p1, p5);  // different description
        assertNotEquals(p1, p6);  // different price
        assertNotEquals(p1, p7);  // different pictureUrl
        assertNotEquals(p1, p8);  // different productBrand
        assertNotEquals(p1, p9);  // different productType
    }
    
    @Test
    void testHashCodeConsistencyForAllResponses() {
        LocalDateTime now = LocalDateTime.now();
        ShippingAddress addr = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        
        OrderResponse or1 = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse or2 = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        assertEquals(or1.hashCode(), or2.hashCode());
        
        BasketItemResponse bir1 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Brand", "Type", 5);
        BasketItemResponse bir2 = new BasketItemResponse(1, "Item", "Desc", 100L, "url", "Brand", "Type", 5);
        assertEquals(bir1.hashCode(), bir2.hashCode());
        
        ProductResponse pr1 = new ProductResponse(1, "Prod", "Desc", 100L, "url", "Brand", "Type");
        ProductResponse pr2 = new ProductResponse(1, "Prod", "Desc", 100L, "url", "Brand", "Type");
        assertEquals(pr1.hashCode(), pr2.hashCode());
    }
    
    @Test
    void testToStringWithNullFields() {
        OrderResponse or = new OrderResponse(null, null, null, null, null, null, null, null);
        String str = or.toString();
        assertNotNull(str);
        assertTrue(str.contains("OrderResponse"));
        
        BasketItemResponse bir = new BasketItemResponse(null, null, null, null, null, null, null, null);
        str = bir.toString();
        assertNotNull(str);
        assertTrue(str.contains("BasketItemResponse"));
        
        ProductResponse pr = new ProductResponse(null, null, null, null, null, null, null);
        str = pr.toString();
        assertNotNull(str);
        assertTrue(str.contains("ProductResponse"));
    }
    
    @Test
    void testAllArgsConstructorForAllResponseClasses() {
        LocalDateTime now = LocalDateTime.now();
        ShippingAddress addr = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        
        OrderResponse or = new OrderResponse(1, "b1", addr, 100L, 10L, 110.0, now, OrderStatus.Pending);
        assertEquals(1, or.getId());
        assertEquals("b1", or.getBasketId());
        assertEquals(addr, or.getShippingAddress());
        assertEquals(100L, or.getSubTotal());
        assertEquals(10L, or.getDeliveryFee());
        assertEquals(110.0, or.getTotal());
        assertEquals(now, or.getOrderDate());
        assertEquals(OrderStatus.Pending, or.getOrderStatus());
        
        BasketItemResponse bir = new BasketItemResponse(2, "Item", "Desc", 200L, "url", "Brand", "Type", 3);
        assertEquals(2, bir.getId());
        assertEquals("Item", bir.getName());
        assertEquals("Desc", bir.getDescription());
        assertEquals(200L, bir.getPrice());
        assertEquals("url", bir.getPictureUrl());
        assertEquals("Brand", bir.getProductBrand());
        assertEquals("Type", bir.getProductType());
        assertEquals(3, bir.getQuantity());
        
        ProductResponse pr = new ProductResponse(3, "Prod", "Description", 300L, "image", "Nike", "Shoes");
        assertEquals(3, pr.getId());
        assertEquals("Prod", pr.getName());
        assertEquals("Description", pr.getDescription());
        assertEquals(300L, pr.getPrice());
        assertEquals("image", pr.getPictureUrl());
        assertEquals("Nike", pr.getProductBrand());
        assertEquals("Shoes", pr.getProductType());
    }
    
    @Test
    void testSettersForAllResponseClasses() {
        OrderResponse or = new OrderResponse();
        or.setId(99);
        or.setBasketId("new-basket");
        or.setSubTotal(500L);
        or.setDeliveryFee(50L);
        or.setTotal(550.0);
        or.setOrderDate(LocalDateTime.now());
        or.setOrderStatus(OrderStatus.PaymentFailed);
        or.setShippingAddress(new ShippingAddress("Jane", "456 Ave", "Unit 2", "Town", "Province", "54321", "Canada"));
        
        assertEquals(99, or.getId());
        assertEquals("new-basket", or.getBasketId());
        assertEquals(500L, or.getSubTotal());
        
        BasketItemResponse bir = new BasketItemResponse();
        bir.setId(88);
        bir.setName("Updated");
        bir.setDescription("New desc");
        bir.setPrice(999L);
        bir.setPictureUrl("new-url");
        bir.setProductBrand("Adidas");
        bir.setProductType("Clothing");
        bir.setQuantity(7);
        
        assertEquals(88, bir.getId());
        assertEquals("Updated", bir.getName());
        assertEquals(999L, bir.getPrice());
        assertEquals(7, bir.getQuantity());
        
        ProductResponse pr = new ProductResponse();
        pr.setId(77);
        pr.setName("New Product");
        pr.setDescription("Brand new");
        pr.setPrice(1500L);
        pr.setPictureUrl("product-image");
        pr.setProductBrand("Puma");
        pr.setProductType("Equipment");
        
        assertEquals(77, pr.getId());
        assertEquals("New Product", pr.getName());
        assertEquals("Brand new", pr.getDescription());
        assertEquals(1500L, pr.getPrice());
    }
    
    @Test
    void testEqualsWithNullAndSameReference() {
        OrderResponse or = new OrderResponse();
        assertEquals(or, or); // same reference
        assertNotEquals(or, null);
        
        BasketItemResponse bir = new BasketItemResponse();
        assertEquals(bir, bir);
        assertNotEquals(bir, null);
        
        ProductResponse pr = new ProductResponse();
        assertEquals(pr, pr);
        assertNotEquals(pr, null);
    }
    
    @Test
    void testBrandResponse_ComprehensiveEqualsAndHashCode() {
        BrandResponse b1 = new BrandResponse(1, "Nike");
        BrandResponse b2 = new BrandResponse(1, "Nike");
        BrandResponse b3 = new BrandResponse(2, "Nike");
        BrandResponse b4 = new BrandResponse(1, "Adidas");
        BrandResponse b5 = new BrandResponse(null, "Nike");
        BrandResponse b6 = new BrandResponse(1, null);
        
        // Test equals
        assertEquals(b1, b2);
        assertNotEquals(b1, b3);  // different id
        assertNotEquals(b1, b4);  // different name
        assertNotEquals(b1, b5);  // null id
        assertNotEquals(b1, b6);  // null name
        assertNotEquals(b1, null);
        assertNotEquals(b1, "String");
        assertEquals(b1, b1);
        
        // Test hashCode consistency
        assertEquals(b1.hashCode(), b2.hashCode());
        
        // Test toString
        String str = b1.toString();
        assertNotNull(str);
        assertTrue(str.contains("Nike"));
        
        // Test with all nulls
        BrandResponse bn = new BrandResponse(null, null);
        assertNotNull(bn.toString());
    }
    
    @Test
    void testTypeResponse_ComprehensiveEqualsAndHashCode() {
        TypeResponse t1 = new TypeResponse(1, "Shoes");
        TypeResponse t2 = new TypeResponse(1, "Shoes");
        TypeResponse t3 = new TypeResponse(2, "Shoes");
        TypeResponse t4 = new TypeResponse(1, "Clothing");
        TypeResponse t5 = new TypeResponse(null, "Shoes");
        TypeResponse t6 = new TypeResponse(1, null);
        
        // Test equals
        assertEquals(t1, t2);
        assertNotEquals(t1, t3);  // different id
        assertNotEquals(t1, t4);  // different name
        assertNotEquals(t1, t5);  // null id
        assertNotEquals(t1, t6);  // null name
        assertNotEquals(t1, null);
        assertNotEquals(t1, new Object());
        assertEquals(t1, t1);
        
        // Test hashCode consistency
        assertEquals(t1.hashCode(), t2.hashCode());
        
        // Test toString
        String str = t1.toString();
        assertNotNull(str);
        assertTrue(str.contains("Shoes"));
        
        // Test with all nulls
        TypeResponse tn = new TypeResponse(null, null);
        assertNotNull(tn.toString());
    }
    
    @Test
    void testAllResponseBuilders() {
        // Test all builders work correctly
        BrandResponse br = BrandResponse.builder().id(1).name("Nike").build();
        assertEquals(1, br.getId());
        assertEquals("Nike", br.getName());
        
        TypeResponse tr = TypeResponse.builder().id(2).name("Shoes").build();
        assertEquals(2, tr.getId());
        assertEquals("Shoes", tr.getName());
        
        // Test builder with partial fields
        BrandResponse br2 = BrandResponse.builder().id(3).build();
        assertEquals(3, br2.getId());
        assertNull(br2.getName());
        
        TypeResponse tr2 = TypeResponse.builder().name("Equipment").build();
        assertNull(tr2.getId());
        assertEquals("Equipment", tr2.getName());
    }
    
    @Test
    void testOrderDto_ComprehensiveFieldTests() {
        LocalDateTime now = LocalDateTime.now();
        ShippingAddress addr = new ShippingAddress("Bob", "789 Blvd", "Suite 1", "Village", "Region", "98765", "USA");
        
        OrderDto dto1 = new OrderDto("basket-1", addr, 250L, 25L, now);
        OrderDto dto2 = new OrderDto("basket-1", addr, 250L, 25L, now);
        OrderDto dto3 = new OrderDto("basket-2", addr, 250L, 25L, now);
        OrderDto dto4 = new OrderDto("basket-1", null, 250L, 25L, now);
        OrderDto dto5 = new OrderDto("basket-1", addr, 300L, 25L, now);
        OrderDto dto6 = new OrderDto("basket-1", addr, 250L, 30L, now);
        OrderDto dto7 = new OrderDto("basket-1", addr, 250L, 25L, null);
        
        // Comprehensive equals tests
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);  // different basketId
        assertNotEquals(dto1, dto4);  // null shippingAddress
        assertNotEquals(dto1, dto5);  // different subTotal
        assertNotEquals(dto1, dto6);  // different deliveryFee
        assertNotEquals(dto1, dto7);  // null orderDate
        assertNotEquals(dto1, null);
        assertEquals(dto1, dto1);
        
        // HashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // ToString
        String str = dto1.toString();
        assertNotNull(str);
        assertTrue(str.contains("basket-1"));
        
        // All nulls
        OrderDto dtoNull = new OrderDto(null, null, null, null, null);
        assertNotNull(dtoNull.toString());
    }
    
    @Test
    void testJwtRequest_ComprehensiveFieldTests() {
        JwtRequest r1 = new JwtRequest("user1", "pass1");
        JwtRequest r2 = new JwtRequest("user1", "pass1");
        JwtRequest r3 = new JwtRequest("user2", "pass1");
        JwtRequest r4 = new JwtRequest("user1", "pass2");
        JwtRequest r5 = new JwtRequest(null, "pass1");
        JwtRequest r6 = new JwtRequest("user1", null);
        
        // Equals tests
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);  // different username
        assertNotEquals(r1, r4);  // different password
        assertNotEquals(r1, r5);  // null username
        assertNotEquals(r1, r6);  // null password
        assertNotEquals(r1, null);
        assertEquals(r1, r1);
        
        // HashCode
        assertEquals(r1.hashCode(), r2.hashCode());
        
        // ToString
        String str = r1.toString();
        assertNotNull(str);
        assertTrue(str.contains("user1"));
        
        // Nulls
        JwtRequest rn = new JwtRequest(null, null);
        assertNotNull(rn.toString());
    }
    
    @Test
    void testJwtResponse_ComprehensiveFieldTests() {
        JwtResponse jr1 = new JwtResponse("token1", "user1");
        JwtResponse jr2 = new JwtResponse("token1", "user1");
        JwtResponse jr3 = new JwtResponse("token2", "user1");
        JwtResponse jr4 = new JwtResponse("token1", "user2");
        JwtResponse jr5 = new JwtResponse(null, "user1");
        JwtResponse jr6 = new JwtResponse("token1", null);
        
        // Equals tests
        assertEquals(jr1, jr2);
        assertNotEquals(jr1, jr3);  // different token
        assertNotEquals(jr1, jr4);  // different username
        assertNotEquals(jr1, jr5);  // null token
        assertNotEquals(jr1, jr6);  // null username
        assertNotEquals(jr1, null);
        assertEquals(jr1, jr1);
        
        // HashCode
        assertEquals(jr1.hashCode(), jr2.hashCode());
        
        // ToString
        String str = jr1.toString();
        assertNotNull(str);
        assertTrue(str.contains("user1"));
        
        // Nulls
        JwtResponse jn = new JwtResponse(null, null);
        assertNotNull(jn.toString());
    }
    
    @Test
    void testCustomErrorResponse_ComprehensiveFieldTests() {
        CustomErrorResponse e1 = new CustomErrorResponse(HttpStatus.BAD_REQUEST, "Error", "Message1");
        CustomErrorResponse e2 = new CustomErrorResponse(HttpStatus.BAD_REQUEST, "Error", "Message1");
        CustomErrorResponse e3 = new CustomErrorResponse(HttpStatus.NOT_FOUND, "Error", "Message1");
        CustomErrorResponse e4 = new CustomErrorResponse(HttpStatus.BAD_REQUEST, "Different", "Message1");
        CustomErrorResponse e5 = new CustomErrorResponse(HttpStatus.BAD_REQUEST, "Error", "Message2");
        CustomErrorResponse e6 = new CustomErrorResponse(null, "Error", "Message1");
        CustomErrorResponse e7 = new CustomErrorResponse(HttpStatus.BAD_REQUEST, null, "Message1");
        CustomErrorResponse e8 = new CustomErrorResponse(HttpStatus.BAD_REQUEST, "Error", null);
        
        // Equals tests
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);  // different status
        assertNotEquals(e1, e4);  // different error
        assertNotEquals(e1, e5);  // different message
        assertNotEquals(e1, e6);  // null status
        assertNotEquals(e1, e7);  // null error
        assertNotEquals(e1, e8);  // null message
        assertNotEquals(e1, null);
        assertEquals(e1, e1);
        
        // HashCode
        assertEquals(e1.hashCode(), e2.hashCode());
        
        // ToString
        String str = e1.toString();
        assertNotNull(str);
        assertTrue(str.contains("Message1"));
        
        // Nulls
        CustomErrorResponse en = new CustomErrorResponse(null, null, null);
        assertNotNull(en.toString());
    }
    
    @Test
    void testBasketResponse_ComprehensiveEqualsAndHashCode() {
        List<BasketItemResponse> items1 = new ArrayList<>();
        items1.add(new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2));
        
        List<BasketItemResponse> items2 = new ArrayList<>();
        items2.add(new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2));
        
        List<BasketItemResponse> items3 = new ArrayList<>();
        items3.add(new BasketItemResponse(2, "Item2", "Desc2", 200L, "pic2.jpg", "Adidas", "Clothing", 1));
        
        BasketResponse b1 = new BasketResponse("basket-1", items1);
        BasketResponse b2 = new BasketResponse("basket-1", items2);
        BasketResponse b3 = new BasketResponse("basket-2", items1);
        BasketResponse b4 = new BasketResponse("basket-1", items3);
        BasketResponse b5 = new BasketResponse("basket-1", null);
        BasketResponse b6 = new BasketResponse(null, items1);
        BasketResponse b7 = new BasketResponse("basket-1", new ArrayList<>());
        
        // Equals tests
        assertEquals(b1, b2);  // equal
        assertNotEquals(b1, b3);  // different id
        assertNotEquals(b1, b4);  // different items
        assertNotEquals(b1, b5);  // null items
        assertNotEquals(b1, b6);  // null id
        assertNotEquals(b1, b7);  // empty items vs non-empty
        assertNotEquals(b1, null);
        assertNotEquals(b1, "String");
        assertEquals(b1, b1);  // same reference
        
        // HashCode
        assertEquals(b1.hashCode(), b2.hashCode());
        
        // ToString
        String str = b1.toString();
        assertNotNull(str);
        assertTrue(str.contains("basket-1"));
        
        // All nulls
        BasketResponse bn = new BasketResponse(null, null);
        assertNotNull(bn.toString());
        assertEquals(bn, bn);
        
        // Builder
        BasketResponse built = BasketResponse.builder()
                .id("test-id")
                .items(items1)
                .build();
        assertEquals("test-id", built.getId());
        assertEquals(1, built.getItems().size());
    }
    
    @Test
    void testOrderResponse_ComprehensiveEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ShippingAddress addr1 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress addr2 = new ShippingAddress("John", "123 St", "Apt 1", "City", "State", "12345", "USA");
        ShippingAddress addr3 = new ShippingAddress("Jane", "456 Ave", "Unit 2", "Town", "Province", "54321", "Canada");
        
        OrderResponse r1 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r2 = new OrderResponse(1, "basket-1", addr2, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r3 = new OrderResponse(2, "basket-1", addr1, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r4 = new OrderResponse(1, "basket-2", addr1, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r5 = new OrderResponse(1, "basket-1", addr3, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r6 = new OrderResponse(1, "basket-1", addr1, 200L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r7 = new OrderResponse(1, "basket-1", addr1, 100L, 20L, 110.0, now, OrderStatus.Pending);
        OrderResponse r8 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, 120.0, now, OrderStatus.Pending);
        OrderResponse r9 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, 110.0, now.plusDays(1), OrderStatus.Pending);
        OrderResponse r10 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, 110.0, now, OrderStatus.PaymentReceived);
        OrderResponse r11 = new OrderResponse(null, "basket-1", addr1, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r12 = new OrderResponse(1, null, addr1, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r13 = new OrderResponse(1, "basket-1", null, 100L, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r14 = new OrderResponse(1, "basket-1", addr1, null, 10L, 110.0, now, OrderStatus.Pending);
        OrderResponse r15 = new OrderResponse(1, "basket-1", addr1, 100L, null, 110.0, now, OrderStatus.Pending);
        OrderResponse r16 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, null, now, OrderStatus.Pending);
        OrderResponse r17 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, 110.0, null, OrderStatus.Pending);
        OrderResponse r18 = new OrderResponse(1, "basket-1", addr1, 100L, 10L, 110.0, now, null);
        
        // Comprehensive equals
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);  // different id
        assertNotEquals(r1, r4);  // different basketId
        assertNotEquals(r1, r5);  // different shippingAddress
        assertNotEquals(r1, r6);  // different subTotal
        assertNotEquals(r1, r7);  // different deliveryFee
        assertNotEquals(r1, r8);  // different total
        assertNotEquals(r1, r9);  // different orderDate
        assertNotEquals(r1, r10); // different orderStatus
        assertNotEquals(r1, r11); // null id
        assertNotEquals(r1, r12); // null basketId
        assertNotEquals(r1, r13); // null shippingAddress
        assertNotEquals(r1, r14); // null subTotal
        assertNotEquals(r1, r15); // null deliveryFee
        assertNotEquals(r1, r16); // null total
        assertNotEquals(r1, r17); // null orderDate
        assertNotEquals(r1, r18); // null orderStatus
        assertNotEquals(r1, null);
        assertEquals(r1, r1);
        
        // HashCode
        assertEquals(r1.hashCode(), r2.hashCode());
        
        // ToString
        assertNotNull(r1.toString());
        assertTrue(r1.toString().contains("basket-1"));
        
        // All nulls
        OrderResponse rn = new OrderResponse(null, null, null, null, null, null, null, null);
        assertNotNull(rn.toString());
    }
    
    @Test
    void testBasketItemResponse_ComprehensiveEqualsAndHashCode() {
        BasketItemResponse i1 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i2 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i3 = new BasketItemResponse(2, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i4 = new BasketItemResponse(1, "Item2", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i5 = new BasketItemResponse(1, "Item1", "Desc2", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i6 = new BasketItemResponse(1, "Item1", "Desc1", 200L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i7 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic2.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i8 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Adidas", "Shoes", 2);
        BasketItemResponse i9 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Clothing", 2);
        BasketItemResponse i10 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 3);
        BasketItemResponse i11 = new BasketItemResponse(null, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i12 = new BasketItemResponse(1, null, "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i13 = new BasketItemResponse(1, "Item1", null, 100L, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i14 = new BasketItemResponse(1, "Item1", "Desc1", null, "pic1.jpg", "Nike", "Shoes", 2);
        BasketItemResponse i15 = new BasketItemResponse(1, "Item1", "Desc1", 100L, null, "Nike", "Shoes", 2);
        BasketItemResponse i16 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", null, "Shoes", 2);
        BasketItemResponse i17 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", null, 2);
        BasketItemResponse i18 = new BasketItemResponse(1, "Item1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes", null);
        
        // Comprehensive equals
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);   // different id
        assertNotEquals(i1, i4);   // different name
        assertNotEquals(i1, i5);   // different description
        assertNotEquals(i1, i6);   // different price
        assertNotEquals(i1, i7);   // different pictureUrl
        assertNotEquals(i1, i8);   // different productBrand
        assertNotEquals(i1, i9);   // different productType
        assertNotEquals(i1, i10);  // different quantity
        assertNotEquals(i1, i11);  // null id
        assertNotEquals(i1, i12);  // null name
        assertNotEquals(i1, i13);  // null description
        assertNotEquals(i1, i14);  // null price
        assertNotEquals(i1, i15);  // null pictureUrl
        assertNotEquals(i1, i16);  // null productBrand
        assertNotEquals(i1, i17);  // null productType
        assertNotEquals(i1, i18);  // null quantity
        assertNotEquals(i1, null);
        assertEquals(i1, i1);
        
        // HashCode
        assertEquals(i1.hashCode(), i2.hashCode());
        
        // ToString
        assertNotNull(i1.toString());
        assertTrue(i1.toString().contains("Item1"));
        
        // All nulls
        BasketItemResponse in = new BasketItemResponse(null, null, null, null, null, null, null, null);
        assertNotNull(in.toString());
    }
    
    @Test
    void testProductResponse_ComprehensiveEqualsAndHashCode() {
        ProductResponse p1 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p2 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p3 = new ProductResponse(2, "Prod1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p4 = new ProductResponse(1, "Prod2", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p5 = new ProductResponse(1, "Prod1", "Desc2", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p6 = new ProductResponse(1, "Prod1", "Desc1", 200L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p7 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic2.jpg", "Nike", "Shoes");
        ProductResponse p8 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic1.jpg", "Adidas", "Shoes");
        ProductResponse p9 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic1.jpg", "Nike", "Clothing");
        ProductResponse p10 = new ProductResponse(null, "Prod1", "Desc1", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p11 = new ProductResponse(1, null, "Desc1", 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p12 = new ProductResponse(1, "Prod1", null, 100L, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p13 = new ProductResponse(1, "Prod1", "Desc1", null, "pic1.jpg", "Nike", "Shoes");
        ProductResponse p14 = new ProductResponse(1, "Prod1", "Desc1", 100L, null, "Nike", "Shoes");
        ProductResponse p15 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic1.jpg", null, "Shoes");
        ProductResponse p16 = new ProductResponse(1, "Prod1", "Desc1", 100L, "pic1.jpg", "Nike", null);
        
        // Comprehensive equals
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);   // different id
        assertNotEquals(p1, p4);   // different name
        assertNotEquals(p1, p5);   // different description
        assertNotEquals(p1, p6);   // different price
        assertNotEquals(p1, p7);   // different pictureUrl
        assertNotEquals(p1, p8);   // different productBrand
        assertNotEquals(p1, p9);   // different productType
        assertNotEquals(p1, p10);  // null id
        assertNotEquals(p1, p11);  // null name
        assertNotEquals(p1, p12);  // null description
        assertNotEquals(p1, p13);  // null price
        assertNotEquals(p1, p14);  // null pictureUrl
        assertNotEquals(p1, p15);  // null productBrand
        assertNotEquals(p1, p16);  // null productType
        assertNotEquals(p1, null);
        assertEquals(p1, p1);
        
        // HashCode
        assertEquals(p1.hashCode(), p2.hashCode());
        
        // ToString
        assertNotNull(p1.toString());
        assertTrue(p1.toString().contains("Prod1"));
        
        // All nulls
        ProductResponse pn = new ProductResponse(null, null, null, null, null, null, null);
        assertNotNull(pn.toString());
    }
}
