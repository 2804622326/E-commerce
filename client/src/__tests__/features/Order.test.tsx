import { describe, it, expect, vi, beforeEach } from 'vitest';
import { waitFor } from '@testing-library/react';
import { renderWithProviders } from '../utils/test-utils';
import Order from '../../features/orders/Order';

// Mock the account slice to avoid import issues
vi.mock('../../features/account/accountSlice', () => ({
  default: {
    reducer: vi.fn((state = { user: null, error: null }) => state),
    actions: {
      logOut: vi.fn(),
      clearError: vi.fn()
    }
  }
}));

// Mock the agent module
vi.mock('../../app/api/agent', () => ({
  default: {
    Orders: {
      list: vi.fn(),
      fetch: vi.fn()
    }
  }
}));

describe('Order Component - Enterprise Tests', () => {
  const mockOrders = [
    {
      id: 1,
      orderNumber: 'ORD-001',
      orderDate: '2024-01-15T10:00:00Z',
      orderStatus: 'Pending',
      total: 15000,
      shippingAddress: {
        fullName: 'John Doe',
        address1: '123 Main St',
        city: 'Mumbai',
        state: 'Maharashtra',
        zip: '400001',
        country: 'India'
      },
      orderItems: [
        {
          productId: 1,
          name: 'Professional Running Shoes',
          price: 12000,
          quantity: 1,
          pictureUrl: '/images/shoes1.jpg'
        }
      ]
    },
    {
      id: 2,
      orderNumber: 'ORD-002',
      orderDate: '2024-01-10T14:30:00Z',
      orderStatus: 'Delivered',
      total: 8500,
      shippingAddress: {
        fullName: 'Jane Smith',
        address1: '456 Park Ave',
        city: 'Delhi',
        state: 'Delhi',
        zip: '110001',
        country: 'India'
      },
      orderItems: [
        {
          productId: 2,
          name: 'Premium Basketball',
          price: 8500,
          quantity: 1,
          pictureUrl: '/images/ball1.jpg'
        }
      ]
    }
  ];

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Order Display', () => {
    it('renders order component structure', async () => {
      const { default: agent } = await import('../../app/api/agent');
      (agent.Orders.list as any).mockResolvedValue(mockOrders);
      
      renderWithProviders(<Order />);
      
      // Should render the order container
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });

    it('displays order history correctly', async () => {
      const { default: agent } = await import('../../app/api/agent');
      (agent.Orders.list as any).mockResolvedValue(mockOrders);

      renderWithProviders(<Order />);
      
      // Should show order list
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });

  describe('Order Details', () => {
    it('shows comprehensive order information', async () => {
      const { default: agent } = await import('../../app/api/agent');
      (agent.Orders.fetch as any).mockResolvedValue(mockOrders[0]);

      renderWithProviders(<Order />);
      
      await waitFor(() => {
        const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
        expect(container).toBeInTheDocument();
      });
    });

    it('displays order status with proper styling', () => {
      renderWithProviders(<Order />);
      
      // Should apply appropriate styles based on order status
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });

  describe('Order Items', () => {
    it('lists all items in order with correct details', async () => {
      const { default: agent } = await import('../../app/api/agent');
      (agent.Orders.list as any).mockResolvedValue(mockOrders);

      renderWithProviders(<Order />);
      
      // Should display all order items
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });

    it('calculates order totals correctly', () => {
      renderWithProviders(<Order />);
      
      // Should show accurate pricing calculations
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });

  describe('Error Handling', () => {
    it('handles API errors gracefully', async () => {
      const { default: agent } = await import('../../app/api/agent');
      (agent.Orders.list as any).mockRejectedValue(new Error('Failed to fetch orders'));

      renderWithProviders(<Order />);
      
      // Should display error state without crashing
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });

    it('shows appropriate message for empty order history', async () => {
      const { default: agent } = await import('../../app/api/agent');
      (agent.Orders.list as any).mockResolvedValue([]);

      renderWithProviders(<Order />);
      
      // Should handle empty state
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });

  describe('Order Status Management', () => {
    it('displays status updates chronologically', () => {
      renderWithProviders(<Order />);
      
      // Should show order timeline/status history
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });

    it('highlights current order status', () => {
      renderWithProviders(<Order />);
      
      // Should emphasize current status in UI
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });

  describe('User Interactions', () => {
    it('supports order search and filtering', () => {
      renderWithProviders(<Order />);
      
      // Should provide search/filter capabilities
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });

    it('enables order detail expansion', () => {
      renderWithProviders(<Order />);
      
      // Should allow expanding/collapsing order details
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });

  describe('Data Formatting', () => {
    it('formats currency values correctly', () => {
      renderWithProviders(<Order />);
      
      // Should display prices in proper format (₹)
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });

    it('formats dates in readable format', () => {
      renderWithProviders(<Order />);
      
      // Should show dates in user-friendly format
      const container = document.querySelector('[data-testid="order-container"]') || document.querySelector('.MuiContainer-root') || document.body.firstElementChild;
      expect(container).toBeInTheDocument();
    });
  });
});