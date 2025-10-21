import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import axios from 'axios';
import basketService from '../../app/api/basketService';
import { setBasket } from '../../features/basket/basketSlice';
import { Basket } from '../../app/models/basket';
import { Product } from '../../app/models/product';

// Mock dependencies
vi.mock('axios');
vi.mock('../../features/basket/basketSlice');
vi.mock('@paralleldrive/cuid2', () => ({
  createId: () => 'test-basket-id-123'
}));

const mockedAxios = axios as any;
const mockDispatch = vi.fn();

// Test data
const mockProduct: Product = {
  id: 1,
  name: 'Test Product',
  price: 100,
  description: 'Test Description',
  pictureUrl: 'test.jpg',
  productBrand: 'Test Brand',
  productType: 'Test Type'
};

const mockBasket: Basket = {
  id: 'test-basket-123',
  items: [
    {
      id: 1,
      name: 'Test Product',
      price: 100,
      description: 'Test Description',
      quantity: 2,
      pictureUrl: 'test.jpg',
      productBrand: 'Test Brand',
      productType: 'Test Type'
    }
  ]
};

describe('BasketService Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    // Clear localStorage before each test
    localStorage.clear();
    // Mock successful axios responses by default
    mockedAxios.get.mockResolvedValue({ data: mockBasket });
    mockedAxios.post.mockResolvedValue({ data: mockBasket });
    mockedAxios.delete.mockResolvedValue({});
  });

  afterEach(() => {
    localStorage.clear();
  });

  describe('getBasketFromApi', () => {
    it('should retrieve basket from API successfully', async () => {
      const result = await basketService.getBasketFromApi();
      
      expect(mockedAxios.get).toHaveBeenCalledWith('http://localhost:8081/api/baskets');
      expect(result).toEqual(mockBasket);
    });

    it('should throw error when API call fails', async () => {
      mockedAxios.get.mockRejectedValue(new Error('Network error'));
      
      await expect(basketService.getBasketFromApi()).rejects.toThrow('Failed to retrieve the basket.');
    });
  });

  describe('getBasket', () => {
    it('should retrieve basket from localStorage successfully', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      const result = await basketService.getBasket();
      
      expect(result).toEqual(mockBasket);
    });

    it('should throw error when basket not found in localStorage', async () => {
      await expect(basketService.getBasket()).rejects.toThrow('Failed to retrieve the basket:');
    });

    it('should handle invalid JSON in localStorage', async () => {
      localStorage.setItem('basket', 'invalid-json');
      
      await expect(basketService.getBasket()).rejects.toThrow('Failed to retrieve the basket:');
    });
  });

  describe('addItemToBasket', () => {
    it('should add new item to existing basket', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      const result = await basketService.addItemToBasket(mockProduct, 1, mockDispatch);
      
      expect(result.basket.items).toHaveLength(1);
      expect(result.basket.items[0].quantity).toBe(3); // 2 existing + 1 new
      expect(result.totals.subTotal).toBe(300); // 3 * 100
      expect(mockDispatch).toHaveBeenCalled();
    });

    it('should create new basket when none exists', async () => {
      const result = await basketService.addItemToBasket(mockProduct, 2, mockDispatch);
      
      expect(result.basket.id).toBe('test-basket-id-123');
      expect(result.basket.items).toHaveLength(1);
      expect(result.basket.items[0].quantity).toBe(2);
      expect(result.totals.subTotal).toBe(200);
    });

    it('should add new product to basket with existing different products', async () => {
      const basketWithDifferentProduct: Basket = {
        id: 'test-basket-123',
        items: [
          {
            id: 999,
            name: 'Different Product',
            price: 50,
            description: 'Different Description',
            quantity: 1,
            pictureUrl: 'different.jpg',
            productBrand: 'Different Brand',
            productType: 'Different Type'
          }
        ]
      };
      localStorage.setItem('basket', JSON.stringify(basketWithDifferentProduct));
      
      const result = await basketService.addItemToBasket(mockProduct, 1, mockDispatch);
      
      expect(result.basket.items).toHaveLength(2);
      expect(result.basket.items[1].id).toBe(1);
      expect(result.basket.items[1].quantity).toBe(1);
    });

    it('should handle errors during basket addition', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      mockedAxios.post.mockRejectedValue(new Error('Server error'));
      
      await expect(basketService.addItemToBasket(mockProduct, 1, mockDispatch))
        .rejects.toThrow('Failed to add and intem to Basket.');
    });
  });

  describe('remove', () => {
    it('should remove item from basket', async () => {
      // Create a basket with two items so removing one leaves one item
      const basketWithTwoItems = {
        id: mockBasket.id,
        items: [
          ...mockBasket.items,
          { ...mockBasket.items[0], id: 2, name: 'Another Product' }
        ]
      };
      localStorage.setItem('basket', JSON.stringify(basketWithTwoItems));
      
      await basketService.remove(1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items).toHaveLength(1);
    });

    it('should clear localStorage when basket becomes empty', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      localStorage.setItem('basket_id', 'test-id');
      
      await basketService.remove(1, mockDispatch);
      
      expect(localStorage.getItem('basket_id')).toBeNull();
      expect(localStorage.getItem('basket')).toBeNull();
    });

    it('should handle removal of non-existing item', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      await basketService.remove(999, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items).toHaveLength(1); // Item should still be there
    });

    it('should handle empty basket gracefully', async () => {
      // No basket in localStorage
      await expect(basketService.remove(1, mockDispatch)).resolves.not.toThrow();
    });
  });

  describe('incrementItemQuantity', () => {
    it('should increment item quantity', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      await basketService.incrementItemQuantity(1, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(3);
    });

    it('should ensure minimum quantity of 1', async () => {
      const basketWithLowQuantity: Basket = {
        ...mockBasket,
        items: [{ ...mockBasket.items[0], quantity: 0 }]
      };
      localStorage.setItem('basket', JSON.stringify(basketWithLowQuantity));
      
      await basketService.incrementItemQuantity(1, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(1);
    });

    it('should handle non-existing item', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      await basketService.incrementItemQuantity(999, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(2); // Original quantity unchanged
    });

    it('should handle empty basket', async () => {
      await expect(basketService.incrementItemQuantity(1, 1, mockDispatch)).resolves.not.toThrow();
    });
  });

  describe('decrementItemQuantity', () => {
    it('should decrement item quantity', async () => {
      const basketWithHighQuantity: Basket = {
        ...mockBasket,
        items: [{ ...mockBasket.items[0], quantity: 5 }]
      };
      localStorage.setItem('basket', JSON.stringify(basketWithHighQuantity));
      
      await basketService.decrementItemQuantity(1, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(4);
    });

    it('should not decrement below quantity 1', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket)); // quantity is 2
      
      await basketService.decrementItemQuantity(1, 5, mockDispatch); // Try to decrement by 5
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(2); // Should remain unchanged
    });

    it('should handle quantity of 1', async () => {
      const basketWithQuantityOne: Basket = {
        ...mockBasket,
        items: [{ ...mockBasket.items[0], quantity: 1 }]
      };
      localStorage.setItem('basket', JSON.stringify(basketWithQuantityOne));
      
      await basketService.decrementItemQuantity(1, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(1); // Should remain 1
    });

    it('should handle non-existing item', async () => {
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      await basketService.decrementItemQuantity(999, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(2); // Original quantity unchanged
    });
  });

  describe('deleteBasket', () => {
    it('should delete basket via API', async () => {
      await basketService.deleteBasket('test-basket-123');
      
      expect(mockedAxios.delete).toHaveBeenCalledWith('http://localhost:8081/api/baskets/test-basket-123');
    });

    it('should throw error when deletion fails', async () => {
      mockedAxios.delete.mockRejectedValue(new Error('Delete failed'));
      
      await expect(basketService.deleteBasket('test-basket-123'))
        .rejects.toThrow('Failed to delete the basket.');
    });
  });

  describe('setBasket', () => {
    it('should update basket via API and localStorage', async () => {
      await basketService.setBasket(mockBasket, mockDispatch);
      
      expect(mockedAxios.post).toHaveBeenCalledWith('http://localhost:8081/api/baskets', mockBasket);
      expect(localStorage.getItem('basket')).toBe(JSON.stringify(mockBasket));
      expect(setBasket).toHaveBeenCalledWith(mockBasket);
      expect(mockDispatch).toHaveBeenCalled();
    });

    it('should throw error when API update fails', async () => {
      mockedAxios.post.mockRejectedValueOnce(new Error('Update failed'));
      
      await expect(basketService.setBasket(mockBasket, mockDispatch))
        .rejects.toThrow('Failed to update basket.');
    });
  });

  describe('Private methods integration', () => {
    it('should calculate totals correctly', async () => {
      const multiItemBasket: Basket = {
        id: 'test',
        items: [
          { ...mockBasket.items[0], quantity: 2, price: 100 }, // 200
          { ...mockBasket.items[0], id: 2, quantity: 3, price: 50 } // 150
        ]
      };
      localStorage.setItem('basket', JSON.stringify(multiItemBasket));
      
      const result = await basketService.addItemToBasket(mockProduct, 1, mockDispatch);
      
      expect(result.totals.subTotal).toBe(450); // 200 + 150 + 100
      expect(result.totals.shipping).toBe(0);
      expect(result.totals.total).toBe(450);
    });

    it('should create basket with correct structure', async () => {
      const result = await basketService.addItemToBasket(mockProduct, 1, mockDispatch);
      
      expect(result.basket.id).toBe('test-basket-id-123');
      expect(result.basket.items).toHaveLength(1);
      expect(localStorage.getItem('basket_id')).toBe('test-basket-id-123');
    });

    it('should map product to basket item correctly', async () => {
      const result = await basketService.addItemToBasket(mockProduct, 2, mockDispatch);
      
      const basketItem = result.basket.items[0];
      expect(basketItem.id).toBe(mockProduct.id);
      expect(basketItem.name).toBe(mockProduct.name);
      expect(basketItem.price).toBe(mockProduct.price);
      expect(basketItem.description).toBe(mockProduct.description);
      expect(basketItem.quantity).toBe(2);
      expect(basketItem.pictureUrl).toBe(mockProduct.pictureUrl);
      expect(basketItem.productBrand).toBe(mockProduct.productBrand);
      expect(basketItem.productType).toBe(mockProduct.productType);
    });
  });
});