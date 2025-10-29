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

    @Test
    void testEntityEqualsWithNullFields() {
        Brand brand1 = new Brand(null, null, null);
        Brand brand2 = new Brand(null, null, null);
        assertEquals(brand1, brand2);
        
        Type type1 = new Type(null, null, null);
        Type type2 = new Type(null, null, null);
        assertEquals(type1, type2);
    }

    @Test
    void testEntityEqualsSameInstance() {
        Brand brand = new Brand(1, "Nike", null);
        assertEquals(brand, brand);
        assertTrue(brand.equals(brand));
        
        Type type = new Type(1, "Shoes", null);
        assertEquals(type, type);
        
        Product product = new Product(1, "Prod", "Desc", 100L, "/pic", null, null);
        assertEquals(product, product);
    }

    @Test
    void testEntityEqualsWithNull() {
        Brand brand = new Brand(1, "Nike", null);
        assertNotEquals(brand, null);
        assertFalse(brand.equals(null));
        
        Type type = new Type(1, "Shoes", null);
        assertNotEquals(type, null);
        
        Product product = new Product(1, "Prod", "Desc", 100L, "/pic", null, null);
        assertNotEquals(product, null);
    }

    @Test
    void testEntityEqualsDifferentClass() {
        Brand brand = new Brand(1, "Nike", null);
        assertNotEquals(brand, "String");
        assertNotEquals(brand, Integer.valueOf(1));
        assertFalse(brand.equals("String"));
        
        Type type = new Type(1, "Shoes", null);
        assertNotEquals(type, new Object());
        
        Product product = new Product(1, "Prod", "Desc", 100L, "/pic", null, null);
        assertNotEquals(product, brand);
    }

    @Test
    void testEntityHashCodeWithNullFields() {
        Brand brand1 = new Brand(null, null, null);
        Brand brand2 = new Brand(null, null, null);
        assertEquals(brand1.hashCode(), brand2.hashCode());
        
        Type type1 = new Type(null, null, null);
        Type type2 = new Type(null, null, null);
        assertEquals(type1.hashCode(), type2.hashCode());
    }

    @Test
    void testEntityToStringWithNullFields() {
        Brand brand = new Brand(null, null, null);
        assertNotNull(brand.toString());
        String str = brand.toString();
        assertTrue(str.contains("Brand") || str.contains("null"));
        
        Type type = new Type(null, null, null);
        assertNotNull(type.toString());
        
        Product product = new Product(null, null, null, null, null, null, null);
        assertNotNull(product.toString());
    }

    @Test
    void testBasketEqualsAndHashCode() {
        Basket basket1 = new Basket("id1");
        Basket basket2 = new Basket("id1");
        
        // Test with same id
        assertEquals(basket1, basket2);
        assertEquals(basket1.hashCode(), basket2.hashCode());
        
        // Test with different id
        Basket basket3 = new Basket("id2");
        assertNotEquals(basket1, basket3);
        
        // Test same instance
        assertEquals(basket1, basket1);
        
        // Test with null
        assertNotEquals(basket1, null);
        
        // Test with different class
        assertNotEquals(basket1, "String");
    }

    @Test
    void testBasketItemEqualsAndHashCode() {
        BasketItem item1 = new BasketItem();
        item1.setId(1);
        item1.setName("Item");
        item1.setPrice(100L);
        
        BasketItem item2 = new BasketItem();
        item2.setId(1);
        item2.setName("Item");
        item2.setPrice(100L);
        
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        
        BasketItem item3 = new BasketItem();
        item3.setId(2);
        assertNotEquals(item1, item3);
        
        assertNotEquals(item1, null);
        assertNotEquals(item1, "String");
    }

    @Test
    void testProductEqualsWithDifferentFields() {
        Brand brand = Brand.builder().id(1).name("Nike").build();
        Type type = Type.builder().id(1).name("Shoes").build();
        
        Product prod1 = new Product(1, "Prod1", "Desc", 100L, "/pic", brand, type);
        Product prod2 = new Product(1, "Prod1", "Desc", 100L, "/pic", brand, type);
        Product prod3 = new Product(2, "Prod2", "Desc", 100L, "/pic", brand, type);
        Product prod4 = new Product(1, "Prod1", "Different", 100L, "/pic", brand, type);
        
        assertEquals(prod1, prod2);
        assertNotEquals(prod1, prod3);
        assertNotEquals(prod1, prod4);
    }

    @Test
    void testBrandEqualsWithDifferentFields() {
        Brand brand1 = new Brand(1, "Nike", null);
        Brand brand2 = new Brand(1, "Nike", null);
        Brand brand3 = new Brand(1, "Adidas", null);
        Brand brand4 = new Brand(2, "Nike", null);
        
        assertEquals(brand1, brand2);
        assertNotEquals(brand1, brand3);
        assertNotEquals(brand1, brand4);
    }

    @Test
    void testTypeEqualsWithDifferentFields() {
        Type type1 = new Type(1, "Shoes", null);
        Type type2 = new Type(1, "Shoes", null);
        Type type3 = new Type(1, "Clothing", null);
        Type type4 = new Type(2, "Shoes", null);
        
        assertEquals(type1, type2);
        assertNotEquals(type1, type3);
        assertNotEquals(type1, type4);
    }

    @Test
    void testProductWithEmptyStrings() {
        Product product = Product.builder()
                .id(1)
                .name("")
                .description("")
                .pictureUrl("")
                .price(0L)
                .build();
        
        assertEquals("", product.getName());
        assertEquals("", product.getDescription());
        assertEquals("", product.getPictureUrl());
        assertEquals(0L, product.getPrice());
    }

    @Test
    void testBasketWithNullId() {
        Basket basket = new Basket();
        assertNull(basket.getId());
        assertNotNull(basket.getItems());
        
        basket.setId(null);
        assertNull(basket.getId());
    }

    @Test
    void testBasketItemWithNullValues() {
        BasketItem item = new BasketItem();
        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getPrice());
        assertNull(item.getPictureUrl());
        assertNull(item.getProductBrand());
        assertNull(item.getProductType());
        assertNull(item.getQuantity());
    }

    @Test
    void testBrandWithEmptyProductList() {
        Brand brand = Brand.builder()
                .id(1)
                .name("Nike")
                .prodcts(new ArrayList<>())
                .build();
        
        assertNotNull(brand.getProdcts());
        assertTrue(brand.getProdcts().isEmpty());
    }

    @Test
    void testTypeWithEmptyProductList() {
        Type type = Type.builder()
                .id(1)
                .name("Shoes")
                .prodcts(new ArrayList<>())
                .build();
        
        assertNotNull(type.getProdcts());
        assertTrue(type.getProdcts().isEmpty());
    }

    @Test
    void testHashCodeConsistency() {
        Brand brand = new Brand(1, "Nike", null);
        int hash1 = brand.hashCode();
        int hash2 = brand.hashCode();
        int hash3 = brand.hashCode();
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
        
        Product product = Product.builder().id(1).name("Test").build();
        hash1 = product.hashCode();
        hash2 = product.hashCode();
        assertEquals(hash1, hash2);
    }
    
    @Test
    void testProduct_ComprehensiveEqualsAndHashCode() {
        Brand nike = new Brand(1, "Nike", null);
        Brand adidas = new Brand(2, "Adidas", null);
        Type shoes = new Type(1, "Shoes", null);
        Type clothing = new Type(2, "Clothing", null);
        
        Product p1 = new Product(1, "Product1", "Desc1", 100L, "pic1.jpg", nike, shoes);
        Product p2 = new Product(1, "Product1", "Desc1", 100L, "pic1.jpg", nike, shoes);
        Product p3 = new Product(2, "Product1", "Desc1", 100L, "pic1.jpg", nike, shoes);
        Product p4 = new Product(1, "Product2", "Desc1", 100L, "pic1.jpg", nike, shoes);
        Product p5 = new Product(1, "Product1", "Desc2", 100L, "pic1.jpg", nike, shoes);
        Product p6 = new Product(1, "Product1", "Desc1", 200L, "pic1.jpg", nike, shoes);
        Product p7 = new Product(1, "Product1", "Desc1", 100L, "pic2.jpg", nike, shoes);
        Product p8 = new Product(1, "Product1", "Desc1", 100L, "pic1.jpg", adidas, shoes);
        Product p9 = new Product(1, "Product1", "Desc1", 100L, "pic1.jpg", nike, clothing);
        Product p10 = new Product(null, "Product1", "Desc1", 100L, "pic1.jpg", nike, shoes);
        Product p11 = new Product(1, null, "Desc1", 100L, "pic1.jpg", nike, shoes);
        Product p12 = new Product(1, "Product1", null, 100L, "pic1.jpg", nike, shoes);
        Product p13 = new Product(1, "Product1", "Desc1", null, "pic1.jpg", nike, shoes);
        Product p14 = new Product(1, "Product1", "Desc1", 100L, null, nike, shoes);
        Product p15 = new Product(1, "Product1", "Desc1", 100L, "pic1.jpg", null, shoes);
        Product p16 = new Product(1, "Product1", "Desc1", 100L, "pic1.jpg", nike, null);
        
        // Comprehensive equals
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);   // different id
        assertNotEquals(p1, p4);   // different name
        assertNotEquals(p1, p5);   // different description
        assertNotEquals(p1, p6);   // different price
        assertNotEquals(p1, p7);   // different pictureUrl
        assertNotEquals(p1, p8);   // different brand
        assertNotEquals(p1, p9);   // different type
        assertNotEquals(p1, p10);  // null id
        assertNotEquals(p1, p11);  // null name
        assertNotEquals(p1, p12);  // null description
        assertNotEquals(p1, p13);  // null price
        assertNotEquals(p1, p14);  // null pictureUrl
        assertNotEquals(p1, p15);  // null brand
        assertNotEquals(p1, p16);  // null type
        assertNotEquals(p1, null);
        assertNotEquals(p1, "String");
        assertEquals(p1, p1);
        
        // HashCode
        assertEquals(p1.hashCode(), p2.hashCode());
        
        // ToString
        assertNotNull(p1.toString());
        assertTrue(p1.toString().contains("Product1"));
        
        // All nulls
        Product pn = new Product(null, null, null, null, null, null, null);
        assertNotNull(pn.toString());
    }
    
    @Test
    void testBrand_ComprehensiveEqualsAndHashCode() {
        Brand b1 = new Brand(1, "Nike", null);
        Brand b2 = new Brand(1, "Nike", null);
        Brand b3 = new Brand(2, "Nike", null);
        Brand b4 = new Brand(1, "Adidas", null);
        Brand b5 = new Brand(null, "Nike", null);
        Brand b6 = new Brand(1, null, null);
        
        // Equals
        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
        assertNotEquals(b1, b4);
        assertNotEquals(b1, b5);
        assertNotEquals(b1, b6);
        assertNotEquals(b1, null);
        assertEquals(b1, b1);
        
        // HashCode
        assertEquals(b1.hashCode(), b2.hashCode());
        
        // ToString
        assertNotNull(b1.toString());
        assertTrue(b1.toString().contains("Nike"));
        
        Brand bn = new Brand(null, null, null);
        assertNotNull(bn.toString());
    }
    
    @Test
    void testType_ComprehensiveEqualsAndHashCode() {
        Type t1 = new Type(1, "Shoes", null);
        Type t2 = new Type(1, "Shoes", null);
        Type t3 = new Type(2, "Shoes", null);
        Type t4 = new Type(1, "Clothing", null);
        Type t5 = new Type(null, "Shoes", null);
        Type t6 = new Type(1, null, null);
        
        // Equals
        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t1, t4);
        assertNotEquals(t1, t5);
        assertNotEquals(t1, t6);
        assertNotEquals(t1, null);
        assertEquals(t1, t1);
        
        // HashCode
        assertEquals(t1.hashCode(), t2.hashCode());
        
        // ToString
        assertNotNull(t1.toString());
        assertTrue(t1.toString().contains("Shoes"));
        
        Type tn = new Type(null, null, null);
        assertNotNull(tn.toString());
    }
    
    @Test
    void testBasket_ComprehensiveEqualsAndHashCode() {
        Basket b1 = new Basket("basket-1");
        Basket b2 = new Basket("basket-1");
        Basket b3 = new Basket("basket-2");
        Basket b4 = new Basket("basket-1");
        b4.setItems(null);
        Basket b5 = new Basket(null);
        
        // Equals
        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
        assertNotEquals(b1, b4);
        assertNotEquals(b1, b5);
        assertNotEquals(b1, null);
        assertEquals(b1, b1);
        
        // HashCode
        assertEquals(b1.hashCode(), b2.hashCode());
        
        // ToString
        assertNotNull(b1.toString());
        assertTrue(b1.toString().contains("basket-1"));
        
        Basket bn = new Basket(null);
        assertNotNull(bn.toString());
    }
    
    @Test
    void testBasketItem_ComprehensiveEqualsAndHashCode() {
        BasketItem bi1 = new BasketItem();
        bi1.setId(1);
        bi1.setName("Item1");
        bi1.setQuantity(2);
        
        BasketItem bi2 = new BasketItem();
        bi2.setId(1);
        bi2.setName("Item1");
        bi2.setQuantity(2);
        
        BasketItem bi3 = new BasketItem();
        bi3.setId(2);
        bi3.setName("Item1");
        bi3.setQuantity(2);
        
        BasketItem bi4 = new BasketItem();
        bi4.setId(1);
        bi4.setName("Item2");
        bi4.setQuantity(2);
        
        BasketItem bi5 = new BasketItem();
        bi5.setId(1);
        bi5.setName("Item1");
        bi5.setQuantity(3);
        
        BasketItem bi6 = new BasketItem();
        bi6.setName("Item1");
        bi6.setQuantity(2);
        
        // Equals
        assertEquals(bi1, bi2);
        assertNotEquals(bi1, bi3);
        assertNotEquals(bi1, bi4);
        assertNotEquals(bi1, bi5);
        assertNotEquals(bi1, bi6);
        assertNotEquals(bi1, null);
        assertEquals(bi1, bi1);
        
        // HashCode
        assertEquals(bi1.hashCode(), bi2.hashCode());
        
        // ToString
        assertNotNull(bi1.toString());
        assertTrue(bi1.toString().contains("Item1"));
        
        BasketItem bin = new BasketItem();
        assertNotNull(bin.toString());
    }
}
