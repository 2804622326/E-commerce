import { describe, it, expect } from 'vitest';

describe('BasketSummary - Calculations', () => {
  const calculateSubtotal = (items: Array<{price: number, quantity: number}>): number => {
    return items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  };

  const calculateTotal = (subtotal: number, shipping: number): number => {
    return subtotal + shipping;
  };

  it('calculates subtotal for single item', () => {
    const items = [{ price: 2500, quantity: 2 }];
    expect(calculateSubtotal(items)).toBe(5000);
  });

  it('calculates subtotal for multiple items', () => {
    const items = [
      { price: 2500, quantity: 2 },
      { price: 800, quantity: 1 }
    ];
    expect(calculateSubtotal(items)).toBe(5800);
  });

  it('calculates total with shipping', () => {
    const subtotal = 5800;
    const shipping = 200;
    expect(calculateTotal(subtotal, shipping)).toBe(6000);
  });

  it('handles empty basket', () => {
    expect(calculateSubtotal([])).toBe(0);
  });

  it('formats INR currency correctly', () => {
    const formatPrice = (price: number): string => {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2
      }).format(price);
    };

    expect(formatPrice(6000)).toBe('₹6,000.00');
    expect(formatPrice(5800)).toBe('₹5,800.00');
    expect(formatPrice(200)).toBe('₹200.00');
  });
});
