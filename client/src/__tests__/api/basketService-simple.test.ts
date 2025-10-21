import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import basketService from '../../app/api/basketService';
import { Basket } from '../../app/models/basket';
import { Product } from '../../app/models/product';

// Mock dependencies
vi.mock('axios', () => ({
  default: {
    get: vi.fn().mockResolvedValue({ data: {} }),
    post: vi.fn().mockResolvedValue({ data: {} }),
    delete: vi.fn().mockResolvedValue({})
  }
}));

vi.mock('../../features/basket/basketSlice', () => ({
  setBasket: vi.fn()
}));

vi.mock('@paralleldrive/cuid2', () => ({
  createId: () => 'test-basket-id-123'
}));

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
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
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
  });

  describe('remove', () => {
    it('should remove item from basket', async () => {
      // Create basket with two items so removal leaves one item
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
      expect(savedBasket.items).toHaveLength(1);
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
      expect(savedBasket.items[0].quantity).toBe(2);
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
      localStorage.setItem('basket', JSON.stringify(mockBasket));
      
      await basketService.decrementItemQuantity(1, 5, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(2);
    });

    it('should handle quantity of 1', async () => {
      const basketWithQuantityOne: Basket = {
        ...mockBasket,
        items: [{ ...mockBasket.items[0], quantity: 1 }]
      };
      localStorage.setItem('basket', JSON.stringify(basketWithQuantityOne));
      
      await basketService.decrementItemQuantity(1, 1, mockDispatch);
      
      const savedBasket = JSON.parse(localStorage.getItem('basket') || '{}');
      expect(savedBasket.items[0].quantity).toBe(1);
    });
  });

  describe('Private method integration tests', () => {
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