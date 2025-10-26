package com.ecommerce.sportscenter.service;

import com.ecommerce.sportscenter.entity.Basket;
import com.ecommerce.sportscenter.entity.BasketItem;
import com.ecommerce.sportscenter.exceptions.BasketAlreadyExistsException;
import com.ecommerce.sportscenter.exceptions.BasketNotFoundException;
import com.ecommerce.sportscenter.model.BasketResponse;
import com.ecommerce.sportscenter.repository.BasketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BasketServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Basket Service Unit Tests")
class BasketServiceImplTest {

    @Mock
    private BasketRepository basketRepository;

    @InjectMocks
    private BasketServiceImpl basketService;

    private Basket testBasket;
    private BasketItem testBasketItem;

    @BeforeEach
    void setUp() {
        testBasketItem = new BasketItem();
        testBasketItem.setId(1);
        testBasketItem.setName("Test Product");
        testBasketItem.setDescription("Test Description");
        testBasketItem.setPrice(10000L);
        testBasketItem.setPictureUrl("/images/products/test.png");
        testBasketItem.setProductBrand("Nike");
        testBasketItem.setProductType("Running");
        testBasketItem.setQuantity(2);

        testBasket = new Basket("test-basket-id");
        testBasket.setItems(Arrays.asList(testBasketItem));
    }

    @Test
    @DisplayName("Should return all baskets successfully")
    void getAllBaskets_ShouldReturnAllBaskets() {
        // Given
        List<Basket> baskets = Arrays.asList(testBasket);
        when(basketRepository.findAll()).thenReturn(baskets);

        // When
        List<BasketResponse> result = basketService.getAllBaskets();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("test-basket-id");
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Test Product");

        verify(basketRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no baskets exist")
    void getAllBaskets_WhenEmpty_ShouldReturnEmptyList() {
        // Given
        when(basketRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<BasketResponse> result = basketService.getAllBaskets();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(basketRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return basket by ID successfully")
    void getBasketById_WhenBasketExists_ShouldReturnBasket() {
        // Given
        String basketId = "test-basket-id";
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(testBasket));

        // When
        BasketResponse result = basketService.getBasketById(basketId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(basketId);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Test Product");
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);

        verify(basketRepository, times(1)).findById(basketId);
    }

    @Test
    @DisplayName("Should throw BasketNotFoundException when basket does not exist")
    void getBasketById_WhenBasketDoesNotExist_ShouldThrowException() {
        // Given
        String basketId = "non-existent-basket";
        when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> basketService.getBasketById(basketId))
                .isInstanceOf(BasketNotFoundException.class)
                .hasMessageContaining("Basket with ID " + basketId + " not found");

        verify(basketRepository, times(1)).findById(basketId);
    }

    @Test
    @DisplayName("Should delete basket by ID successfully")
    void deleteBasketById_ShouldCallRepositoryDelete() {
        // Given
        String basketId = "test-basket-id";
        when(basketRepository.existsById(basketId)).thenReturn(true);
        doNothing().when(basketRepository).deleteById(basketId);

        // When
        basketService.deleteBasketById(basketId);

        // Then
        verify(basketRepository, times(1)).existsById(basketId);
        verify(basketRepository, times(1)).deleteById(basketId);
    }

    @Test
    @DisplayName("Should throw BasketNotFoundException when deleting non-existent basket")
    void deleteBasketById_WhenBasketDoesNotExist_ShouldThrowException() {
        // Given
        String basketId = "non-existent-basket";
        when(basketRepository.existsById(basketId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> basketService.deleteBasketById(basketId))
                .isInstanceOf(BasketNotFoundException.class)
                .hasMessageContaining("Basket with ID " + basketId + " not found");

        verify(basketRepository, times(1)).existsById(basketId);
        verify(basketRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should create basket successfully")
    void createBasket_ShouldReturnCreatedBasket() {
        // Given
        Basket newBasket = new Basket("new-basket-id");
        when(basketRepository.existsById("new-basket-id")).thenReturn(false);
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);

        // When
        BasketResponse result = basketService.createBasket(newBasket);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("test-basket-id");
        assertThat(result.getItems()).hasSize(1);

        verify(basketRepository, times(1)).existsById("new-basket-id");
        verify(basketRepository, times(1)).save(newBasket);
    }

    @Test
    @DisplayName("Should throw BasketAlreadyExistsException when creating basket with existing ID")
    void createBasket_WhenBasketExists_ShouldThrowException() {
        // Given
        Basket existingBasket = new Basket("existing-basket-id");
        when(basketRepository.existsById("existing-basket-id")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> basketService.createBasket(existingBasket))
                .isInstanceOf(BasketAlreadyExistsException.class)
                .hasMessageContaining("Basket with ID existing-basket-id already exists");

        verify(basketRepository, times(1)).existsById("existing-basket-id");
        verify(basketRepository, never()).save(any(Basket.class));
    }

    @Test
    @DisplayName("Should create basket with empty items")
    void createBasket_WithEmptyItems_ShouldReturnBasketWithEmptyItems() {
        // Given
        Basket emptyBasket = new Basket("empty-basket-id");
        when(basketRepository.save(any(Basket.class))).thenReturn(emptyBasket);

        // When
        BasketResponse result = basketService.createBasket(emptyBasket);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("empty-basket-id");
        assertThat(result.getItems()).isEmpty();

        verify(basketRepository, times(1)).save(emptyBasket);
    }

    @Test
    @DisplayName("Should handle basket with multiple items")
    void getBasketById_WithMultipleItems_ShouldReturnAllItems() {
        // Given
        BasketItem secondItem = new BasketItem();
        secondItem.setId(2);
        secondItem.setName("Second Product");
        secondItem.setQuantity(3);
        
        testBasket.setItems(Arrays.asList(testBasketItem, secondItem));
        when(basketRepository.findById("test-basket-id")).thenReturn(Optional.of(testBasket));

        // When
        BasketResponse result = basketService.getBasketById("test-basket-id");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Test Product");
        assertThat(result.getItems().get(1).getName()).isEqualTo("Second Product");

        verify(basketRepository, times(1)).findById("test-basket-id");
    }

    @Test
    @DisplayName("Should correctly map all basket item properties")
    void getBasketById_ShouldMapAllBasketItemProperties() {
        // Given
        when(basketRepository.findById("test-basket-id")).thenReturn(Optional.of(testBasket));

        // When
        BasketResponse result = basketService.getBasketById("test-basket-id");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems().get(0).getId()).isEqualTo(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Test Product");
        assertThat(result.getItems().get(0).getDescription()).isEqualTo("Test Description");
        assertThat(result.getItems().get(0).getPrice()).isEqualTo(10000L);
        assertThat(result.getItems().get(0).getPictureUrl()).isEqualTo("/images/products/test.png");
        assertThat(result.getItems().get(0).getProductBrand()).isEqualTo("Nike");
        assertThat(result.getItems().get(0).getProductType()).isEqualTo("Running");
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);

        verify(basketRepository, times(1)).findById("test-basket-id");
    }
}
