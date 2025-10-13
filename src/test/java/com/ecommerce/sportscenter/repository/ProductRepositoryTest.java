package com.ecommerce.sportscenter.repository;

import com.ecommerce.sportscenter.entity.Brand;
import com.ecommerce.sportscenter.entity.Product;
import com.ecommerce.sportscenter.entity.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for ProductRepository
 * Uses H2 in-memory database for testing
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Brand testBrand;
    private Type testType;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Create and persist test brand
        testBrand = Brand.builder()
                .name("Nike")
                .build();
        testBrand = brandRepository.save(testBrand);

        // Create and persist test type
        testType = Type.builder()
                .name("Running")
                .build();
        testType = typeRepository.save(testType);

        // Create and persist test product
        testProduct = Product.builder()
                .name("Test Running Shoes")
                .description("High quality running shoes")
                .price(15000L)
                .pictureUrl("/images/products/shoes.png")
                .brand(testBrand)
                .type(testType)
                .build();
        testProduct = productRepository.save(testProduct);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save product successfully")
    void save_ShouldPersistProduct() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .description("New Description")
                .price(20000L)
                .pictureUrl("/images/products/new.png")
                .brand(testBrand)
                .type(testType)
                .build();

        // When
        Product saved = productRepository.save(newProduct);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Product");
        assertThat(saved.getPrice()).isEqualTo(20000L);
    }

    @Test
    @DisplayName("Should find product by ID")
    void findById_WhenProductExists_ShouldReturnProduct() {
        // When
        Optional<Product> found = productRepository.findById(testProduct.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Running Shoes");
        assertThat(found.get().getBrand().getName()).isEqualTo("Nike");
        assertThat(found.get().getType().getName()).isEqualTo("Running");
    }

    @Test
    @DisplayName("Should return empty when product does not exist")
    void findById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Product> found = productRepository.findById(9999);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all products with pagination")
    void findAll_WithPagination_ShouldReturnPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Product> products = productRepository.findAll(pageable);

        // Then
        assertThat(products).isNotNull();
        assertThat(products.getContent()).isNotEmpty();
        assertThat(products.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should filter products by brand using specification")
    void findAll_WithBrandFilter_ShouldReturnFilteredProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("brand").get("id"), testBrand.getId());

        // When
        Page<Product> products = productRepository.findAll(spec, pageable);

        // Then
        assertThat(products).isNotNull();
        assertThat(products.getContent()).isNotEmpty();
        assertThat(products.getContent().get(0).getBrand().getName()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("Should filter products by type using specification")
    void findAll_WithTypeFilter_ShouldReturnFilteredProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type").get("id"), testType.getId());

        // When
        Page<Product> products = productRepository.findAll(spec, pageable);

        // Then
        assertThat(products).isNotNull();
        assertThat(products.getContent()).isNotEmpty();
        assertThat(products.getContent().get(0).getType().getName()).isEqualTo("Running");
    }

    @Test
    @DisplayName("Should filter products by name using specification")
    void findAll_WithNameFilter_ShouldReturnFilteredProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%Running%");

        // When
        Page<Product> products = productRepository.findAll(spec, pageable);

        // Then
        assertThat(products).isNotNull();
        assertThat(products.getContent()).isNotEmpty();
        assertThat(products.getContent().get(0).getName()).contains("Running");
    }

    @Test
    @DisplayName("Should combine multiple specifications")
    void findAll_WithMultipleFilters_ShouldReturnFilteredProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = Specification
                .where((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("brand").get("id"), testBrand.getId()))
                .and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("type").get("id"), testType.getId()))
                .and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("name"), "%Running%"));

        // When
        Page<Product> products = productRepository.findAll(spec, pageable);

        // Then
        assertThat(products).isNotNull();
        assertThat(products.getContent()).isNotEmpty();
        assertThat(products.getContent().get(0).getBrand().getName()).isEqualTo("Nike");
        assertThat(products.getContent().get(0).getType().getName()).isEqualTo("Running");
        assertThat(products.getContent().get(0).getName()).contains("Running");
    }

    @Test
    @DisplayName("Should delete product by ID")
    void deleteById_ShouldRemoveProduct() {
        // Given
        Integer productId = testProduct.getId();

        // When
        productRepository.deleteById(productId);
        entityManager.flush();

        // Then
        Optional<Product> deleted = productRepository.findById(productId);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should update product successfully")
    void save_WhenUpdatingProduct_ShouldUpdateFields() {
        // Given
        Product product = productRepository.findById(testProduct.getId()).orElseThrow();
        product.setName("Updated Name");
        product.setPrice(25000L);

        // When
        Product updated = productRepository.save(product);
        entityManager.flush();
        entityManager.clear();

        // Then
        Product found = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Updated Name");
        assertThat(found.getPrice()).isEqualTo(25000L);
    }

    @Test
    @DisplayName("Should count all products")
    void count_ShouldReturnTotalCount() {
        // When
        long count = productRepository.count();

        // Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Should check if product exists by ID")
    void existsById_WhenProductExists_ShouldReturnTrue() {
        // When
        boolean exists = productRepository.existsById(testProduct.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when product does not exist")
    void existsById_WhenProductDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = productRepository.existsById(9999);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should handle multiple products with same brand")
    void findAll_WithMultipleProductsSameBrand_ShouldReturnAll() {
        // Given
        Product secondProduct = Product.builder()
                .name("Another Nike Product")
                .description("Another description")
                .price(18000L)
                .pictureUrl("/images/products/another.png")
                .brand(testBrand)
                .type(testType)
                .build();
        productRepository.save(secondProduct);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Product> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("brand").get("id"), testBrand.getId());

        // When
        Page<Product> products = productRepository.findAll(spec, pageable);

        // Then
        assertThat(products).isNotNull();
        assertThat(products.getContent()).hasSize(2);
        assertThat(products.getContent()).allMatch(p -> p.getBrand().getName().equals("Nike"));
    }
}
