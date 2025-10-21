import { describe, it, expect } from 'vitest';

describe('Utility Functions Tests', () => {
  describe('Price Formatting', () => {
    const formatPrice = (price: number): string => {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2
      }).format(price);
    };

    it('should format price with INR currency symbol', () => {
      expect(formatPrice(1000)).toBe('₹1,000.00');
    });

    it('should format decimal prices correctly', () => {
      expect(formatPrice(2500.50)).toBe('₹2,500.50');
    });

    it('should format zero correctly', () => {
      expect(formatPrice(0)).toBe('₹0.00');
    });

    it('should format large numbers with proper separators', () => {
      expect(formatPrice(1234567)).toBe('₹12,34,567.00');
    });
  });

  describe('Image Path Extraction', () => {
    const extractImageName = (pictureUrl: string): string | null => {
      if (pictureUrl) {
        const parts = pictureUrl.split('/');
        if (parts.length > 0) {
          return parts[parts.length - 1];
        }
      }
      return null;
    };

    it('should extract filename from full URL', () => {
      expect(extractImageName('/images/products/tennis-racket.jpg')).toBe('tennis-racket.jpg');
    });

    it('should handle simple filename', () => {
      expect(extractImageName('product.png')).toBe('product.png');
    });

    it('should return null for empty string', () => {
      expect(extractImageName('')).toBeNull();
    });

    it('should extract from complex path', () => {
      expect(extractImageName('/api/v1/images/products/basketball.jpg')).toBe('basketball.jpg');
    });
  });

  describe('Basket Calculations', () => {
    const calculateSubtotal = (items: Array<{price: number, quantity: number}>): number => {
      return items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    };

    it('should calculate subtotal for single item', () => {
      const items = [{ price: 2500, quantity: 2 }];
      expect(calculateSubtotal(items)).toBe(5000);
    });

    it('should calculate subtotal for multiple items', () => {
      const items = [
        { price: 2500, quantity: 2 },
        { price: 800, quantity: 1 }
      ];
      expect(calculateSubtotal(items)).toBe(5800);
    });

    it('should return 0 for empty basket', () => {
      expect(calculateSubtotal([])).toBe(0);
    });

    it('should handle zero quantity', () => {
      const items = [{ price: 1000, quantity: 0 }];
      expect(calculateSubtotal(items)).toBe(0);
    });
  });
});
