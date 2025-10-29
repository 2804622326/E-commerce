import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { getBasketFromLocalStorage } from '../../app/util/util';

// Mock console.error to track error calls
const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

describe('getBasketFromLocalStorage Utility Function', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();
    consoleErrorSpy.mockClear();
  });

  afterEach(() => {
    // Clean up after each test
    localStorage.clear();
  });

  describe('Valid Basket Data', () => {
    it('returns parsed basket when valid JSON is stored', () => {
      const mockBasket = {
        id: 'test-basket-123',
        items: [
          {
            id: 1,
            name: 'Test Product',
            price: 100,
            quantity: 2,
            pictureUrl: '/images/test.jpg',
            productBrand: 'Test Brand',
            productType: 'Test Type',
            description: 'Test Description'
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(mockBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(mockBasket);
      expect(result?.id).toBe('test-basket-123');
      expect(result?.items).toHaveLength(1);
      expect(result?.items[0].name).toBe('Test Product');
    });

    it('returns basket with multiple items', () => {
      const mockBasket = {
        id: 'multi-item-basket',
        items: [
          {
            id: 1,
            name: 'Product 1',
            price: 100,
            quantity: 1,
            pictureUrl: '/images/product1.jpg',
            productBrand: 'Brand 1',
            productType: 'Type 1',
            description: 'Description 1'
          },
          {
            id: 2,
            name: 'Product 2',
            price: 200,
            quantity: 3,
            pictureUrl: '/images/product2.jpg',
            productBrand: 'Brand 2',
            productType: 'Type 2',
            description: 'Description 2'
          },
          {
            id: 3,
            name: 'Product 3',
            price: 50,
            quantity: 5,
            pictureUrl: '/images/product3.jpg',
            productBrand: 'Brand 3',
            productType: 'Type 3',
            description: 'Description 3'
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(mockBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(mockBasket);
      expect(result?.items).toHaveLength(3);
      expect(result?.items[1].quantity).toBe(3);
      expect(result?.items[2].price).toBe(50);
    });

    it('returns empty basket when basket has no items', () => {
      const emptyBasket = {
        id: 'empty-basket',
        items: []
      };

      localStorage.setItem('basket', JSON.stringify(emptyBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(emptyBasket);
      expect(result?.items).toHaveLength(0);
      expect(result?.id).toBe('empty-basket');
    });

    it('handles basket with complex item properties', () => {
      const complexBasket = {
        id: 'complex-basket',
        items: [
          {
            id: 999,
            name: 'Complex Product with Special Characters: café & résumé',
            price: 1234.99,
            quantity: 10,
            pictureUrl: 'https://example.com/images/special-product.jpg',
            productBrand: 'Brand with Spaces & Numbers 123',
            productType: 'Electronics > Computers > Laptops',
            description: 'A very detailed description with\nnewlines and special chars: @#$%^&*()'
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(complexBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(complexBasket);
      expect(result?.items[0].name).toContain('café');
      expect(result?.items[0].price).toBe(1234.99);
      expect(result?.items[0].description).toContain('\n');
    });
  });

  describe('No Storage Data', () => {
    it('returns null when localStorage is empty', () => {
      // localStorage is already cleared in beforeEach
      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });

    it('returns null when basket key does not exist', () => {
      // Set some other keys but not 'basket'
      localStorage.setItem('user', JSON.stringify({ name: 'test' }));
      localStorage.setItem('settings', JSON.stringify({ theme: 'dark' }));

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });

    it('returns null when basket key exists but value is null', () => {
      localStorage.setItem('basket', 'null');

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });

    it('returns null when basket key exists but value is empty string', () => {
      localStorage.setItem('basket', '');

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });
  });

  describe('Invalid JSON Data', () => {
    it('returns null and logs error for malformed JSON', () => {
      localStorage.setItem('basket', '{invalid json}');

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Error Parsing basket from local storage: ',
        expect.any(SyntaxError)
      );
    });

    it('returns null and logs error for incomplete JSON', () => {
      localStorage.setItem('basket', '{"id": "test", "items": [');

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Error Parsing basket from local storage: ',
        expect.any(SyntaxError)
      );
    });

    it('returns null and logs error for non-JSON string', () => {
      localStorage.setItem('basket', 'this is not json at all');

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Error Parsing basket from local storage: ',
        expect.any(SyntaxError)
      );
    });

    it('returns null and logs error for truncated JSON', () => {
      const truncatedJson = '{"id": "test-basket", "items": [{"id": 1, "name": "Product"';
      localStorage.setItem('basket', truncatedJson);

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Error Parsing basket from local storage: ',
        expect.any(SyntaxError)
      );
    });

    it('returns null and logs error for JSON with syntax errors', () => {
      const badJson = '{"id": "test", "items": [{"id": 1 "name": "missing comma"}]}';
      localStorage.setItem('basket', badJson);

      const result = getBasketFromLocalStorage();

      expect(result).toBeNull();
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Error Parsing basket from local storage: ',
        expect.any(SyntaxError)
      );
    });
  });

  describe('Edge Cases', () => {
    it('handles very large basket data', () => {
      const largeBasket = {
        id: 'large-basket',
        items: Array.from({ length: 100 }, (_, i) => ({
          id: i + 1,
          name: `Product ${i + 1}`,
          price: Math.round(Math.random() * 1000 * 100) / 100,
          quantity: Math.floor(Math.random() * 10) + 1,
          pictureUrl: `/images/product-${i + 1}.jpg`,
          productBrand: `Brand ${i + 1}`,
          productType: `Type ${i + 1}`,
          description: `Description for product ${i + 1}`
        }))
      };

      localStorage.setItem('basket', JSON.stringify(largeBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(largeBasket);
      expect(result?.items).toHaveLength(100);
      expect(result?.items[99].name).toBe('Product 100');
    });

    it('handles basket with special numeric values', () => {
      const specialValuesBasket = {
        id: 'special-values',
        items: [
          {
            id: 1,
            name: 'Zero Price Product',
            price: 0,
            quantity: 0,
            pictureUrl: '',
            productBrand: '',
            productType: '',
            description: ''
          },
          {
            id: 2,
            name: 'High Precision Price',
            price: 99.999999,
            quantity: 1,
            pictureUrl: '/images/precise.jpg',
            productBrand: 'Precise Brand',
            productType: 'Precise Type',
            description: 'Precise description'
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(specialValuesBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(specialValuesBasket);
      expect(result?.items[0].price).toBe(0);
      expect(result?.items[0].quantity).toBe(0);
      expect(result?.items[1].price).toBe(99.999999);
    });

    it('handles basket with null and undefined-like values', () => {
      const nullValuesBasket = {
        id: 'null-values',
        items: [
          {
            id: 1,
            name: 'Product with nulls',
            price: 100,
            quantity: 1,
            pictureUrl: null,
            productBrand: null,
            productType: null,
            description: null
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(nullValuesBasket));

      const result = getBasketFromLocalStorage();

      expect(result).toEqual(nullValuesBasket);
      expect(result?.items[0].pictureUrl).toBeNull();
      expect(result?.items[0].productBrand).toBeNull();
    });

    it('maintains data types correctly after parsing', () => {
      const typedBasket = {
        id: 'typed-basket',
        items: [
          {
            id: 42,
            name: 'Type Test Product',
            price: 123.45,
            quantity: 7,
            pictureUrl: '/images/type-test.jpg',
            productBrand: 'Type Brand',
            productType: 'Type Category',
            description: 'Type description'
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(typedBasket));

      const result = getBasketFromLocalStorage();

      expect(result?.id).toBe('typed-basket');
      expect(typeof result?.id).toBe('string');
      expect(result?.items[0].id).toBe(42);
      expect(typeof result?.items[0].id).toBe('number');
      expect(result?.items[0].price).toBe(123.45);
      expect(typeof result?.items[0].price).toBe('number');
      expect(result?.items[0].quantity).toBe(7);
      expect(typeof result?.items[0].quantity).toBe('number');
    });
  });

  describe('LocalStorage Behavior', () => {
    it('does not modify localStorage during read operations', () => {
      const originalBasket = {
        id: 'read-only-test',
        items: [
          {
            id: 1,
            name: 'Read Only Product',
            price: 50,
            quantity: 2,
            pictureUrl: '/images/readonly.jpg',
            productBrand: 'ReadOnly Brand',
            productType: 'ReadOnly Type',
            description: 'ReadOnly description'
          }
        ]
      };

      localStorage.setItem('basket', JSON.stringify(originalBasket));
      const storedValueBefore = localStorage.getItem('basket');

      getBasketFromLocalStorage();

      const storedValueAfter = localStorage.getItem('basket');
      expect(storedValueAfter).toBe(storedValueBefore);
    });

    it('works correctly with multiple localStorage operations', () => {
      // Set initial basket
      const basket1 = { id: 'basket1', items: [] };
      localStorage.setItem('basket', JSON.stringify(basket1));
      
      let result = getBasketFromLocalStorage();
      expect(result?.id).toBe('basket1');

      // Update basket
      const basket2 = { id: 'basket2', items: [] };
      localStorage.setItem('basket', JSON.stringify(basket2));
      
      result = getBasketFromLocalStorage();
      expect(result?.id).toBe('basket2');

      // Remove basket
      localStorage.removeItem('basket');
      
      result = getBasketFromLocalStorage();
      expect(result).toBeNull();
    });
  });
});