package com.ecommerce.sportscenter.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Entity classes
 * Tests all Lombok generated methods and entity behavior
 */
class EntityTest {

    @Test
    void testBrand_AllMethods() {
        Brand brand = Brand.builder()
                .id(1)
                .name("Nike")
                .build();
        
        assertEquals(1, brand.getId());
        assertEquals("Nike", brand.getName());
        
        brand.setId(2);
        brand.setName("Adidas");
        
        assertEquals(2, brand.getId());
        assertEquals("Adidas", brand.getName());
        
        Brand emptyBrand = new Brand();
        assertNull(emptyBrand.getId());
        assertNull(emptyBrand.getName());
        
        List<Product> products = new ArrayList<>();
        brand.setProdcts(products);
        assertEquals(products, brand.getProdcts());
        
        assertNotNull(brand.toString());
        assertTrue(brand.toString().contains("Adidas"));
        
        Brand brand1 = new Brand(1, "Nike", null);
        Brand brand2 = new Brand(1, "Nike", null);
        assertEquals(brand1, brand2);
        assertEquals(brand1.hashCode(), brand2.hashCode());
    }

    @Test
    void testType_AllMethods() {
        Type type = Type.builder()
                .id(1)
                .name("Shoes")
                .build();
        
        assertEquals(1, type.getId());
        assertEquals("Shoes", type.getName());
        
        type.setId(2);
        type.setName("Clothing");
        
        assertEquals(2, type.getId());
        assertEquals("Clothing", type.getName());
        
        Type emptyType = new Type();
        assertNull(emptyType.getId());
        
        List<Product> products = new ArrayList<>();
        type.setProdcts(products);
        assertEquals(products, type.getProdcts());
        
        assertNotNull(type.toString());
        
        Type type1 = new Type(1, "Shoes", null);
        Type type2 = new Type(1, "Shoes", null);
        assertEquals(type1, type2);
    }

    @Test
    void testProduct_AllMethods() {
        Brand brand = Brand.builder().id(1).name("Nike").build();
        Type type = Type.builder().id(1).name("Shoes").build();
        
        Product product = Product.builder()
                .id(1)
                .name("Running Shoes")
                .description("Comfortable shoes")
                .price(8999L)
                .pictureUrl("/images/shoes.jpg")
                .brand(brand)
                .type(type)
                .build();
        
        assertEquals(1, product.getId());
        assertEquals("Running Shoes", product.getName());
        assertEquals("Comfortable shoes", product.getDescription());
        assertEquals(8999L, product.getPrice());
        assertEquals("/images/shoes.jpg", product.getPictureUrl());
        assertEquals(brand, product.getBrand());
        assertEquals(type, product.getType());
        
        product.setId(2);
        product.setName("New Shoes");
        product.setDescription("New Description");
        product.setPrice(5000L);
        product.setPictureUrl("/new.jpg");
        
        Brand newBrand = Brand.builder().id(2).name("Adidas").build();
        Type newType = Type.builder().id(2).name("Clothing").build();
        product.setBrand(newBrand);
        product.setType(newType);
        
        assertEquals(2, product.getId());
        assertEquals("New Shoes", product.getName());
        assertEquals("New Description", product.getDescription());
        assertEquals(5000L, product.getPrice());
        assertEquals("/new.jpg", product.getPictureUrl());
        assertEquals(newBrand, product.getBrand());
        assertEquals(newType, product.getType());
        
        Product emptyProduct = new Product();
        assertNull(emptyProduct.getId());
        
        Product fullProduct = new Product(1, "Name", "Desc", 1000L, "/pic.jpg", brand, type);
        assertEquals("Name", fullProduct.getName());
        
        assertNotNull(product.toString());
        assertEquals(product, product);
    }

    @Test
    void testBasket_AllMethods() {
        Basket basket = new Basket("basket-123");
        
        assertEquals("basket-123", basket.getId());
        assertNotNull(basket.getItems());
        assertTrue(basket.getItems().isEmpty());
        
        basket.setId("basket-456");
        assertEquals("basket-456", basket.getId());
        
        List<BasketItem> items = new ArrayList<>();
        BasketItem item1 = new BasketItem();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setQuantity(2);
        items.add(item1);
        
        basket.setItems(items);
        assertEquals(1, basket.getItems().size());
        assertEquals("Item 1", basket.getItems().get(0).getName());
        
        Basket emptyBasket = new Basket();
        assertNull(emptyBasket.getId());
        assertNotNull(emptyBasket.getItems());
        
        assertNotNull(basket.toString());
        assertTrue(basket.toString().contains("basket-456"));
        
        assertEquals(basket, basket);
        assertEquals(basket.hashCode(), basket.hashCode());
    }

    @Test
    void testBasketItem_AllMethods() {
        BasketItem item = new BasketItem();
        
        item.setId(1);
        item.setName("Product Name");
        item.setDescription("Product Description");
        item.setPrice(1999L);
        item.setPictureUrl("/images/product.jpg");
        item.setProductBrand("Nike");
        item.setProductType("Shoes");
        item.setQuantity(3);
        
        assertEquals(1, item.getId());
        assertEquals("Product Name", item.getName());
        assertEquals("Product Description", item.getDescription());
        assertEquals(1999L, item.getPrice());
        assertEquals("/images/product.jpg", item.getPictureUrl());
        assertEquals("Nike", item.getProductBrand());
        assertEquals("Shoes", item.getProductType());
        assertEquals(3, item.getQuantity());
        
        assertNotNull(item.toString());
        assertTrue(item.toString().contains("Product Name"));
        
        BasketItem item2 = new BasketItem();
        item2.setId(1);
        item2.setName("Product Name");
        item2.setDescription("Product Description");
        item2.setPrice(1999L);
        item2.setPictureUrl("/images/product.jpg");
        item2.setProductBrand("Nike");
        item2.setProductType("Shoes");
        item2.setQuantity(3);
        
        assertEquals(item, item2);
        assertEquals(item.hashCode(), item2.hashCode());
    }

    @Test
    void testBasketWithMultipleItems() {
        Basket basket = new Basket("multi-basket");
        List<BasketItem> items = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            BasketItem item = new BasketItem();
            item.setId(i);
            item.setName("Item " + i);
            item.setPrice(1000L * i);
            item.setQuantity(i);
            items.add(item);
        }
        
        basket.setItems(items);
        
        assertEquals(5, basket.getItems().size());
        assertEquals("Item 1", basket.getItems().get(0).getName());
        assertEquals("Item 5", basket.getItems().get(4).getName());
        assertEquals(5000L, basket.getItems().get(4).getPrice());
        assertEquals(5, basket.getItems().get(4).getQuantity());
    }

    @Test
    void testProductWithNullBrandAndType() {
        Product product = Product.builder()
                .id(1)
                .name("Product")
                .price(1000L)
                .build();
        
        assertNull(product.getBrand());
        assertNull(product.getType());
        
        product.setBrand(null);
        product.setType(null);
        
        assertNull(product.getBrand());
        assertNull(product.getType());
    }

    @Test
    void testBrandWithProducts() {
        Brand brand = new Brand();
        brand.setId(1);
        brand.setName("Nike");
        
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Shoe 1");
        product1.setBrand(brand);
        
        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Shoe 2");
        product2.setBrand(brand);
        
        products.add(product1);
        products.add(product2);
        
        brand.setProdcts(products);
        
        assertEquals(2, brand.getProdcts().size());
        assertEquals("Shoe 1", brand.getProdcts().get(0).getName());
        assertEquals(brand, brand.getProdcts().get(0).getBrand());
    }

    @Test
    void testTypeWithProducts() {
        Type type = new Type();
        type.setId(1);
        type.setName("Shoes");
        
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Running Shoe");
        product1.setType(type);
        
        products.add(product1);
        type.setProdcts(products);
        
        assertEquals(1, type.getProdcts().size());
        assertEquals("Running Shoe", type.getProdcts().get(0).getName());
    }

    @Test
    void testEntityEquality() {
        Brand brand1 = new Brand(1, "Nike", null);
        Brand brand2 = new Brand(1, "Nike", null);
        Brand brand3 = new Brand(2, "Adidas", null);
        
        assertEquals(brand1, brand2);
        assertNotEquals(brand1, brand3);
        assertNotEquals(brand1, null);
        assertNotEquals(brand1, "string");
        
        Type type1 = new Type(1, "Shoes", null);
        Type type2 = new Type(1, "Shoes", null);
        
        assertEquals(type1, type2);
        
        Product prod1 = new Product(1, "Prod", "Desc", 100L, "/pic", brand1, type1);
        Product prod2 = new Product(1, "Prod", "Desc", 100L, "/pic", brand1, type1);
        
        assertEquals(prod1, prod2);
    }

    @Test
    void testEntityHashCode() {
        Brand brand1 = new Brand(1, "Nike", null);
        Brand brand2 = new Brand(1, "Nike", null);
        
        assertEquals(brand1.hashCode(), brand2.hashCode());
        
        BasketItem item1 = new BasketItem();
        item1.setId(1);
        item1.setName("Item");
        
        BasketItem item2 = new BasketItem();
        item2.setId(1);
        item2.setName("Item");
        
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testBasketItemWithAllFields() {
        BasketItem item = new BasketItem();
        item.setId(99);
        item.setName("Complete Product");
        item.setDescription("Full description with all details");
        item.setPrice(99999L);
        item.setPictureUrl("https://example.com/image.jpg");
        item.setProductBrand("Premium Brand");
        item.setProductType("Premium Type");
        item.setQuantity(10);
        
        assertAll(
            () -> assertEquals(99, item.getId()),
            () -> assertEquals("Complete Product", item.getName()),
            () -> assertEquals("Full description with all details", item.getDescription()),
            () -> assertEquals(99999L, item.getPrice()),
            () -> assertEquals("https://example.com/image.jpg", item.getPictureUrl()),
            () -> assertEquals("Premium Brand", item.getProductBrand()),
            () -> assertEquals("Premium Type", item.getProductType()),
            () -> assertEquals(10, item.getQuantity())
        );
    }
}
