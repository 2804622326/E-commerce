package com.ecommerce.sportscenter.repository;

import com.ecommerce.sportscenter.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should find product by ID")
    void findById_ShouldReturnProduct() {
        Product product = Product.builder().id(1).name("Test").price(100L).build();
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Optional<Product> result = productRepository.findById(1);

        assertThat(result).isPresent();
        verify(productRepository).findById(1);
    }

    @Test
    @DisplayName("Should save product")
    void save_ShouldPersistProduct() {
        Product product = Product.builder().id(1).name("Test").price(100L).build();
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productRepository.save(product);

        assertThat(result).isNotNull();
        verify(productRepository).save(product);
    }
}
