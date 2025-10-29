import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import Review from '../../features/checkout/Review';
import { basketSlice } from '../../features/basket/basketSlice';
import { accountSlice } from '../../features/account/accountSlice';

// Mock BasketSummary component
vi.mock('../../features/basket/BasketSummary', () => ({
  default: () => <div data-testid="basket-summary">Basket Summary Component</div>
}));

const mockBasketWithItems = {
  id: 'test-basket',
  items: [
    {
      id: 1,
      name: 'Test Product 1',
      price: 100,
      description: 'Test Description 1',
      quantity: 2,
      pictureUrl: '/images/products/test-product-1.jpg',
      productBrand: 'Test Brand 1',
      productType: 'Test Type 1'
    },
    {
      id: 2,
      name: 'Test Product 2',
      price: 200,
      description: 'Test Description 2',
      quantity: 1,
      pictureUrl: '/images/products/test-product-2.jpg',
      productBrand: 'Test Brand 2',
      productType: 'Test Type 2'
    }
  ]
};

const mockEmptyBasket = {
  id: 'empty-basket',
  items: []
};

const createTestStore = (basketState: any) => {
  return configureStore({
    reducer: {
      basket: basketSlice.reducer,
      account: accountSlice.reducer
    },
    preloadedState: {
      basket: { basket: basketState },
      account: { user: null, error: null }
    }
  });
};

const renderWithStore = (basketState: any) => {
  const store = createTestStore(basketState);
  return render(
    <Provider store={store}>
      <Review />
    </Provider>
  );
};

describe('Review Component', () => {
  it('renders order summary heading', () => {
    renderWithStore(mockBasketWithItems);
    
    expect(screen.getByText('Order summary')).toBeInTheDocument();
  });

  it('renders table headers correctly', () => {
    renderWithStore(mockBasketWithItems);
    
    expect(screen.getByText('Product Image')).toBeInTheDocument();
    expect(screen.getByText('Product')).toBeInTheDocument();
    expect(screen.getByText('Price')).toBeInTheDocument();
  });

  it('displays all basket items in table', () => {
    renderWithStore(mockBasketWithItems);
    
    expect(screen.getByText('Test Product 1')).toBeInTheDocument();
    expect(screen.getByText('Test Product 2')).toBeInTheDocument();
  });

  it('formats prices correctly in INR currency', () => {
    renderWithStore(mockBasketWithItems);
    
    expect(screen.getByText('₹100.00')).toBeInTheDocument();
    expect(screen.getByText('₹200.00')).toBeInTheDocument();
  });

  it('displays product images with correct src', () => {
    renderWithStore(mockBasketWithItems);
    
    const images = screen.getAllByAltText('Product');
    expect(images).toHaveLength(2);
    expect(images[0]).toHaveAttribute('src', '/images/products/test-product-1.jpg');
    expect(images[1]).toHaveAttribute('src', '/images/products/test-product-2.jpg');
  });

  it('sets correct image dimensions', () => {
    renderWithStore(mockBasketWithItems);
    
    const images = screen.getAllByAltText('Product');
    images.forEach(image => {
      expect(image).toHaveAttribute('width', '50');
      expect(image).toHaveAttribute('height', '50');
    });
  });

  it('renders BasketSummary component', () => {
    renderWithStore(mockBasketWithItems);
    
    expect(screen.getByTestId('basket-summary')).toBeInTheDocument();
  });

  it('handles empty basket gracefully', () => {
    renderWithStore(mockEmptyBasket);
    
    expect(screen.getByText('Order summary')).toBeInTheDocument();
    expect(screen.getByText('Product Image')).toBeInTheDocument();
    expect(screen.getByText('Product')).toBeInTheDocument();
    expect(screen.getByText('Price')).toBeInTheDocument();
    
    // Should not display any product rows
    expect(screen.queryByText('Test Product 1')).not.toBeInTheDocument();
    expect(screen.queryByText('Test Product 2')).not.toBeInTheDocument();
  });

  it('handles null basket state', () => {
    renderWithStore(null);
    
    expect(screen.getByText('Order summary')).toBeInTheDocument();
    expect(screen.getByTestId('basket-summary')).toBeInTheDocument();
  });

  it('extracts image names correctly from complex URLs', () => {
    const basketWithComplexUrls = {
      id: 'test-basket',
      items: [
        {
          id: 1,
          name: 'Complex URL Product',
          price: 150,
          description: 'Test Description',
          quantity: 1,
          pictureUrl: 'https://example.com/path/to/complex-image-name.jpg',
          productBrand: 'Test Brand',
          productType: 'Test Type'
        }
      ]
    };

    renderWithStore(basketWithComplexUrls);
    
    const image = screen.getByAltText('Product');
    expect(image).toHaveAttribute('src', '/images/products/complex-image-name.jpg');
  });

  it('handles items without picture URLs', () => {
    const basketWithoutImages = {
      id: 'test-basket',
      items: [
        {
          id: 1,
          name: 'No Image Product',
          price: 100,
          description: 'Test Description',
          quantity: 1,
          pictureUrl: '',
          productBrand: 'Test Brand',
          productType: 'Test Type'
        }
      ]
    };

    renderWithStore(basketWithoutImages);
    
    expect(screen.getByText('No Image Product')).toBeInTheDocument();
    expect(screen.queryByAltText('Product')).not.toBeInTheDocument();
  });

  it('formats different price values correctly', () => {
    const basketWithVariousPrices = {
      id: 'test-basket',
      items: [
        {
          id: 1,
          name: 'Cheap Product',
          price: 5.50,
          description: 'Test Description',
          quantity: 1,
          pictureUrl: '/images/products/cheap.jpg',
          productBrand: 'Test Brand',
          productType: 'Test Type'
        },
        {
          id: 2,
          name: 'Expensive Product',
          price: 1234.99,
          description: 'Test Description',
          quantity: 1,
          pictureUrl: '/images/products/expensive.jpg',
          productBrand: 'Test Brand',
          productType: 'Test Type'
        }
      ]
    };

    renderWithStore(basketWithVariousPrices);
    
    expect(screen.getByText('₹5.50')).toBeInTheDocument();
    expect(screen.getByText('₹1,234.99')).toBeInTheDocument();
  });

  it('displays table structure correctly', () => {
    renderWithStore(mockBasketWithItems);
    
    const table = screen.getByRole('table');
    expect(table).toBeInTheDocument();
    
    const rows = screen.getAllByRole('row');
    expect(rows).toHaveLength(3); // 1 header + 2 data rows
  });

  it('handles extractImageName edge cases', () => {
    const basketWithEdgeCases = {
      id: 'test-basket',
      items: [
        {
          id: 1,
          name: 'Edge Case Product',
          price: 100,
          description: 'Test Description',
          quantity: 1,
          pictureUrl: 'single-name.jpg', // No slashes
          productBrand: 'Test Brand',
          productType: 'Test Type'
        }
      ]
    };

    renderWithStore(basketWithEdgeCases);
    
    const image = screen.getByAltText('Product');
    expect(image).toHaveAttribute('src', '/images/products/single-name.jpg');
  });
});