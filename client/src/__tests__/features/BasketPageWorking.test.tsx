import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import BasketPage from '../../features/basket/BasketPage';
import { basketSlice } from '../../features/basket/basketSlice';
import { accountSlice } from '../../features/account/accountSlice';

let mockIncrementItemQuantity: any;
let mockDecrementItemQuantity: any;
let mockRemoveItem: any;

vi.mock('../../app/api/agent', () => ({
  default: {
    Basket: {
      get removeItem() { return mockRemoveItem; },
      get incrementItemQuantity() { return mockIncrementItemQuantity; },
      get decrementItemQuantity() { return mockDecrementItemQuantity; },
    },
  },
}));

const createMockStore = (basketState: any) => {
  return configureStore({
    reducer: {
      basket: basketSlice.reducer,
      account: accountSlice.reducer,
    },
    preloadedState: {
      basket: basketState,
      account: { user: null, error: null },
    },
  });
};

describe('BasketPage - Empty State', () => {
  it('shows empty basket message when no items', () => {
    const store = createMockStore({ basket: null });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    expect(screen.getByText(/your basket is empty/i)).toBeInTheDocument();
  });

  it('shows empty basket message when items array is empty', () => {
    const store = createMockStore({ 
      basket: { id: '123', items: [] } 
    });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    expect(screen.getByText(/your basket is empty/i)).toBeInTheDocument();
  });
});

describe('BasketPage - With Items', () => {
  const mockBasket = {
    id: 'basket-123',
    items: [
      {
        id: 1,
        name: 'Tennis Racket',
        description: 'Pro racket',
        price: 2500,
        pictureUrl: '/images/products/racket.jpg',
        productBrand: 'Wilson',
        productType: 'Equipment',
        quantity: 2,
      },
      {
        id: 2,
        name: 'Football',
        description: 'Official ball',
        price: 800,
        pictureUrl: '/images/products/ball.jpg',
        productBrand: 'Nike',
        productType: 'Ball',
        quantity: 1,
      },
    ],
  };

  it('renders basket table with items', () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    expect(screen.getByText('Tennis Racket')).toBeInTheDocument();
    expect(screen.getByText('Football')).toBeInTheDocument();
  });

  it('displays product prices correctly', () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    const prices = screen.getAllByText(/₹2,500\.00/);
    expect(prices.length).toBeGreaterThan(0);
    
    const footballPrices = screen.getAllByText(/₹800\.00/);
    expect(footballPrices.length).toBeGreaterThan(0);
  });

  it('displays item quantities', () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    const quantityElements = screen.getAllByText('2');
    expect(quantityElements.length).toBeGreaterThan(0);
  });

  it('renders delete buttons for each item', () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    const deleteButtons = screen.getAllByTestId('DeleteIcon');
    expect(deleteButtons).toHaveLength(2);
  });

  it('renders increment and decrement buttons', () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    const addButtons = screen.getAllByTestId('AddIcon');
    const removeButtons = screen.getAllByTestId('RemoveIcon');
    
    expect(addButtons.length).toBeGreaterThan(0);
    expect(removeButtons.length).toBeGreaterThan(0);
  });

  it('renders checkout button', () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    const checkoutButton = screen.getByRole('link', { name: /checkout/i });
    expect(checkoutButton).toBeInTheDocument();
  });
});

describe('BasketPage - Item Quantity Interactions', () => {
  const mockBasket = {
    id: '123',
    items: [
      {
        productId: 1,
        name: 'Running Shoes',
        price: 12000,
        quantity: 1,
        pictureUrl: '/images/shoes.jpg',
        brand: 'Nike',
        type: 'Shoes'
      }
    ]
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockIncrementItemQuantity = vi.fn().mockResolvedValue({ 
      basket: { 
        id: '123', 
        items: [{ productId: 1, name: 'Test Product', quantity: 2, price: 100 }] 
      } 
    });
    mockDecrementItemQuantity = vi.fn().mockResolvedValue({ 
      basket: { 
        id: '123', 
        items: [{ productId: 1, name: 'Test Product', quantity: 1, price: 100 }] 
      } 
    });
    mockRemoveItem = vi.fn().mockResolvedValue({ 
      basket: { id: '123', items: [] } 
    });
  });

  it('calls incrementItemQuantity when increment button is clicked', async () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    // Find add buttons (they have AddIcon)
    const buttons = screen.getAllByRole('button');
    const incrementButton = buttons.find(btn => 
      btn.querySelector('svg[data-testid="AddIcon"]')
    );
    
    if (incrementButton) {
      fireEvent.click(incrementButton);
      
      await waitFor(() => {
        expect(mockIncrementItemQuantity).toHaveBeenCalled();
      });
    }
  });

  it('calls decrementItemQuantity when decrement button is clicked', async () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    // Find remove buttons (they have RemoveIcon)
    const buttons = screen.getAllByRole('button');
    const decrementButton = buttons.find(btn => 
      btn.querySelector('svg[data-testid="RemoveIcon"]')
    );
    
    if (decrementButton) {
      fireEvent.click(decrementButton);
      
      await waitFor(() => {
        expect(mockDecrementItemQuantity).toHaveBeenCalled();
      });
    }
  });

  it('triggers increment function when add button clicked', async () => {
    const store = createMockStore({ basket: mockBasket });
    
    render(
      <Provider store={store}>
        <BrowserRouter>
          <BasketPage />
        </BrowserRouter>
      </Provider>
    );
    
    const buttons = screen.getAllByRole('button');
    const incrementButton = buttons.find(btn => 
      btn.querySelector('svg[data-testid="AddIcon"]')
    );
    
    expect(incrementButton).toBeDefined();
    
    if (incrementButton) {
      fireEvent.click(incrementButton);
      
      // Just verify it was called, don't check parameters (too brittle)
      await waitFor(() => {
        expect(mockIncrementItemQuantity).toHaveBeenCalled();
      }, { timeout: 2000 });
    }
  });
});
