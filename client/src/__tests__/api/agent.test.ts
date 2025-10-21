import { describe, it, expect, vi, beforeEach } from 'vitest';
import agent from '../../app/api/agent';
import basketService from '../../app/api/basketService';

// Mock dependencies
vi.mock('../../app/api/basketService');

describe('API Agent Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Basket API', () => {
    it('should get basket successfully', async () => {
      const mockBasket = { id: '123', items: [] };
      vi.mocked(basketService.getBasket).mockResolvedValue(mockBasket);

      const result = await agent.Basket.get();

      expect(basketService.getBasket).toHaveBeenCalled();
      expect(result).toEqual(mockBasket);
    });

    it('should handle get basket error', async () => {
      const error = new Error('Basket not found');
      vi.mocked(basketService.getBasket).mockRejectedValue(error);
      
      await expect(agent.Basket.get()).rejects.toThrow('Basket not found');
    });

    it('should add item to basket successfully', async () => {
      const mockProduct = { 
        id: 1, 
        name: 'Test Product', 
        price: 100,
        description: 'Test description',
        pictureUrl: 'test.jpg',
        productBrand: 'Test Brand',
        productType: 'Test Type'
      };
      const mockDispatch = vi.fn();
      const mockResult = { 
        basket: { id: '123', items: [] }, 
        totals: { shipping: 0, subTotal: 100, total: 100 }
      };
      
      vi.mocked(basketService.addItemToBasket).mockResolvedValue(mockResult);

      const result = await agent.Basket.addItem(mockProduct, mockDispatch);

      expect(basketService.addItemToBasket).toHaveBeenCalledWith(mockProduct, 1, mockDispatch);
      expect(result).toEqual(mockResult);
    });

    it('should handle add item error', async () => {
      const mockProduct = { 
        id: 1, 
        name: 'Test Product', 
        price: 100,
        description: 'Test description',
        pictureUrl: 'test.jpg',
        productBrand: 'Test Brand',
        productType: 'Test Type'
      };
      const mockDispatch = vi.fn();
      const error = new Error('Failed to add item');
      
      vi.mocked(basketService.addItemToBasket).mockRejectedValue(error);

      await expect(agent.Basket.addItem(mockProduct, mockDispatch)).rejects.toThrow('Failed to add item');
    });

    it('should remove item from basket successfully', async () => {
      const mockDispatch = vi.fn();
      vi.mocked(basketService.remove).mockResolvedValue();

      await agent.Basket.removeItem(1, mockDispatch);

      expect(basketService.remove).toHaveBeenCalledWith(1, mockDispatch);
    });

    it('should handle remove item error', async () => {
      const mockDispatch = vi.fn();
      const error = new Error('Failed to remove item');
      
      vi.mocked(basketService.remove).mockRejectedValue(error);

      await expect(agent.Basket.removeItem(1, mockDispatch)).rejects.toThrow('Failed to remove item');
    });

    it('should increment item quantity successfully', async () => {
      const mockDispatch = vi.fn();
      vi.mocked(basketService.incrementItemQuantity).mockResolvedValue();

      await agent.Basket.incrementItemQuantity(1, 2, mockDispatch);

      expect(basketService.incrementItemQuantity).toHaveBeenCalledWith(1, 2, mockDispatch);
    });

    it('should increment item quantity with default value', async () => {
      const mockDispatch = vi.fn();
      vi.mocked(basketService.incrementItemQuantity).mockResolvedValue();

      await agent.Basket.incrementItemQuantity(1, undefined, mockDispatch);

      expect(basketService.incrementItemQuantity).toHaveBeenCalledWith(1, 1, mockDispatch);
    });

    it('should handle increment quantity error', async () => {
      const mockDispatch = vi.fn();
      const error = new Error('Failed to increment');
      
      vi.mocked(basketService.incrementItemQuantity).mockRejectedValue(error);

      await expect(agent.Basket.incrementItemQuantity(1, 1, mockDispatch)).rejects.toThrow('Failed to increment');
    });

    it('should decrement item quantity successfully', async () => {
      const mockDispatch = vi.fn();
      vi.mocked(basketService.decrementItemQuantity).mockResolvedValue();

      await agent.Basket.decrementItemQuantity(1, 2, mockDispatch);

      expect(basketService.decrementItemQuantity).toHaveBeenCalledWith(1, 2, mockDispatch);
    });

    it('should decrement item quantity with default value', async () => {
      const mockDispatch = vi.fn();
      vi.mocked(basketService.decrementItemQuantity).mockResolvedValue();

      await agent.Basket.decrementItemQuantity(1, undefined, mockDispatch);

      expect(basketService.decrementItemQuantity).toHaveBeenCalledWith(1, 1, mockDispatch);
    });

    it('should handle decrement quantity error', async () => {
      const mockDispatch = vi.fn();
      const error = new Error('Failed to decrement');
      
      vi.mocked(basketService.decrementItemQuantity).mockRejectedValue(error);

      await expect(agent.Basket.decrementItemQuantity(1, 1, mockDispatch)).rejects.toThrow('Failed to decrement');
    });

    it('should set basket successfully', async () => {
      const mockBasket = { id: '123', items: [] };
      const mockDispatch = vi.fn();
      vi.mocked(basketService.setBasket).mockResolvedValue();

      await agent.Basket.setBasket(mockBasket, mockDispatch);

      expect(basketService.setBasket).toHaveBeenCalledWith(mockBasket, mockDispatch);
    });

    it('should handle set basket error', async () => {
      const mockBasket = { id: '123', items: [] };
      const mockDispatch = vi.fn();
      const error = new Error('Failed to set basket');
      
      vi.mocked(basketService.setBasket).mockRejectedValue(error);

      await expect(agent.Basket.setBasket(mockBasket, mockDispatch)).rejects.toThrow('Failed to set basket');
    });

    it('should delete basket successfully', async () => {
      vi.mocked(basketService.deleteBasket).mockResolvedValue();

      await agent.Basket.deleteBasket('basket123');

      expect(basketService.deleteBasket).toHaveBeenCalledWith('basket123');
    });

    it('should handle delete basket error', async () => {
      const error = new Error('Failed to delete');
      vi.mocked(basketService.deleteBasket).mockRejectedValue(error);

      await expect(agent.Basket.deleteBasket('basket123')).rejects.toThrow('Failed to delete');
    });
  });
});