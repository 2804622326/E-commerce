import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import BasketPage from '../../features/basket/BasketPage';
import { basketSlice } from '../../features/basket/basketSlice';
import { accountSlice } from '../../features/account/accountSlice';

vi.mock('../../app/api/agent', () => ({
  default: {
    Basket: {
      removeItem: vi.fn(),
      incrementItemQuantity: vi.fn(),
      decrementItemQuantity: vi.fn(),
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
